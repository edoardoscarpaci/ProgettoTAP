from pyspark.sql.functions import *
from pyspark.sql.types import *
from pyspark.streaming import StreamingContext
from pyspark.sql.dataframe import DataFrame


from pyspark import SparkContext
from pyspark.sql.session import SparkSession
from pyspark.conf import SparkConf
from pyspark import SparkContext
from pyspark.sql.session import SparkSession
from pyspark.sql.functions import from_json
import pyspark.sql.types as tp
from pyspark.ml import Pipeline
from pyspark.ml.feature import StopWordsRemover, Word2Vec, RegexTokenizer, PCA
from pyspark.ml.classification import LogisticRegression
from pyspark import SparkContext
from pyspark.sql import SparkSession
from elasticsearch import Elasticsearch
import pyspark

spark = SparkSession.builder.master("local[2]") \
  .getOrCreate()
spark.sparkContext.setLogLevel("ERROR")


# In[6]:


es_mapping = {
    "mappings": {
        "properties": {
            "Nome": {
                "type": "text",
                "fields": {
                    "keyword": {
                        "type": "keyword",
                            "ignore_above": 256
                    }
                }
            },
            "vector": {
                    "type": "dense_vector",
                    "dims": 200
            },
            "pca": {
                "type": "point",
				"fields": {
                    "keyword": {
                        "type": "keyword",
                            "ignore_above": 256
                    }
                }
            },
        }
    }
}

es = Elasticsearch(hosts="http://10.0.100.51:9200") 
response = es.indices.create(
    index="vectorsteps",
    body=es_mapping,
    ignore=400 # ignore 400 already exists code
)
if 'acknowledged' in response:
    if response['acknowledged'] == True:
        print ("INDEX MAPPING SUCCESS FOR INDEX:", response['index'])


# In[7]:


schema = StructType([ \
    StructField("Link",StringType(),True), \
    StructField("Steps",StringType(),True), \
    StructField("Ingredienti",ArrayType(StructType([
         StructField('Nome', StringType(), True),
         StructField('Peso', StringType(), True),
         StructField('Note', StringType(), True)
         ])),True), \
    StructField("Nome", StringType(), True)
  ])


# In[8]:


tr = spark.read\
  .format("kafka")\
  .option("subscribe", "CleanedRecipes")\
  .option("kafka.bootstrap.servers", "10.0.100.23:9092")\
  .option("kafka.request.timeout.ms", "60000")\
  .option("kafka.session.timeout.ms", "60000")\
  .option("failOnDataLoss", "true")\
  .option("startingOffsets", "earliest") \
  .option("partition", 1) \
  .option("fetch.message.max.bytes","52428800")\
  .option("kafka.max.poll.records", "1000")\
  .load()\
  .limit(1000)\

tr = tr.selectExpr("CAST(value AS STRING)")
tr = tr.select(from_json("value", schema).alias("data")) \
    .select("data.*")    
tr


# In[9]:


tr.count()


# In[10]:


stage_1 = RegexTokenizer(inputCol= 'Steps' , outputCol= 'steptokens', pattern= '\\W')
# define stage 2: remove the stop words
stage_2 = StopWordsRemover(inputCol= 'steptokens', outputCol= 'filtered_words')
# define stage 3: create a word vector of the size 100
stage_3 = Word2Vec(inputCol= 'filtered_words', outputCol= 'vector', vectorSize= 200)
# setup the pipeline

pca = PCA(k=2, inputCol="vector")
pca.setOutputCol("pca")

pipeline = Pipeline(stages= [stage_1, stage_2, stage_3,pca])


# In[11]:


pipelineFit = pipeline.fit(tr)


# In[12]:


ds = pipelineFit.transform(tr).select("Nome","pca")


# In[13]:


ds.show()


# In[14]:


df = spark.readStream\
  .format("kafka")\
  .option("subscribe", "CleanedRecipes")\
  .option("kafka.bootstrap.servers", "10.0.100.23:9092")\
  .option("kafka.request.timeout.ms", "60000")\
  .option("kafka.session.timeout.ms", "60000")\
  .option("failOnDataLoss", "true")\
  .option("startingOffsets", "earliest") \
  .option("partition", 1) \
  .option("fetch.message.max.bytes","52428800")\
  .load()\
  .selectExpr("CAST(value AS STRING)")\
  .select(from_json("value", schema).alias("data")) \
  .select("data.*")    

df = pipelineFit.transform(df).select("Nome","vector","pca")


# In[15]:


import pyspark.sql.functions as F
import pyspark.sql.types as T


to_array = F.udf(lambda v: v.toArray().tolist(), T.ArrayType(T.FloatType()))
df = df.withColumn('pca', to_array('pca'))
df = df.withColumn('vector', to_array('vector'))
df


# In[ ]:


df.writeStream \
    .outputMode("append") \
    .format("org.elasticsearch.spark.sql")\
    .option("es.nodes","10.0.100.51")\
    .option("checkpointLocation", "/save/locatio") \
    .start("vectorsteps") \
    .awaitTermination()
 
spark.streams.awaitAnyTermination()


# In[ ]:




