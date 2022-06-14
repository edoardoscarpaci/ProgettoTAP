#!/bin/bash

curl -XDELETE 'http://10.0.100.51:9200/cleanedrecipes'
curl -X PUT "http://10.0.100.51:9200/cleanedrecipes?pretty" -H 'Content-Type: application/json' -d'
{
"mappings": {
	"properties": {
	  "Ingredienti": {
		"type": "nested"
	  },
	  "Link": {
		"type": "text",
		"fields": {
		  "keyword": {
			"type": "keyword",
			"ignore_above": 256
		  }
		}
	  },
	  "Nome": {
		"type": "text",
		"fields": {
		  "keyword": {
			"type": "keyword",
			"ignore_above": 256
		  }
		}
	  },
	  "Steps": {
		"type": "text",
		"fields": {
		  "keyword": {
			"type": "keyword",
			"ignore_above": 256
		  }
		}
	  }
	}
  }
}
'
