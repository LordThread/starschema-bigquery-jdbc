# Query Transformation Engine #

Since version 1.3 the driver is extended with the Query Transformation Engine (QTE).

The purpose of the QTE is to transform usual SQL statements to a special form which BigQuery can handle. There are several restrictions in the SQL compatibility of BigQuery, nearly all of them are handled with the new Query Transformation Engine.

With this feature you can use any traditional SQL Editor, ETL or BI tool without the need for hand-writing custom SQL queries.

For the complete list of supported tools please check the bottom of this page, and our wiki pages with detailed information for each application.

# Features #

  * Using SELECT '`*`' is fully supported in any part of the query. The QTE replaces the '`*`' with the columns for the table/tables/subquery. BigQuery only supports it the subqueries.

  * In Google BigQuery listing the tables in the _FROM_ expression means we want to union them. In every other tool that make queries, listing tables means we want to aggregate them somehow. We make joins by the following rules:
    1. If there is a table related expression in the _WHERE_ we use it as an _ON_ clause for the _JOIN_ expression.
    1. If there are more than two tables we'll make a _JOIN_ with a subquery + table, where the subquery contains the two joined tables.
    1. If the _ON_ clause is missing for the JOIN, we Cartesian join them by adding `true AS equivalent_column`

  * Extension of missing datasets. Many tools (and humans) create queries like this one: `SELECT column(s) FROM table` where the schema name (dataset in BigQuery perspective) is not specified. The QTE solves this by looking for the available projects and use the first suitable project for the table.

  * Sequential JOIN support, so you can chain your chains. By default BigQuery can only handle the so-called small joins (details in the Query Reference), with the help of QTE you can make any number of _JOINs_!
```
SELECT * FROM table1 JOIN table2 ON table1.id = table2.id JOIN table3 ON table1.id = table3.id ... and so on
```

> Please note, if a program [(like SAP Crystal Report)](SAPCrystalReports.md) use parenthesis for joins, a maximum of eleven tables can be joined, it's the limit of the [Query Transformation Engine](QueryTransformationEngine.md)
```
SELECT * FROM ((table1 JOIN table2 ON table1.id = table2.id) JOIN table3 ON table1.id = table3.id)
```

  * If the parsing fails somehow, the driver sends the original query to BigQuery without modification and logs the error.

  * If you want to write your own queries, you can easily turn off the Query Transformation Engine by using the transfromQuery parameter of the driver. Examples can be found in the [JDBC Url wiki page](JDBCURL.md)

  * Currently tested and working with the following tools:
    1. [SAP Crystal Reports](SAPCrystalReports.md)
    1. [Eclipse Birt](eclipseBiRT.md)
    1. [i-net Clear Reports](inetClearReports.md)
    1. [Razor SQL](RazorSQL.md)

> If you have any other tool that we should include or test, please contact with us.


# Samples #


  1. Making join from the expression found in _WHERE_
    * Input
```
SELECT *
 FROM efashion.ARTICLE_LOOKUP a, efashion.ARTICLE_COLOR_LOOKUP b 
WHERE (a.ARTICLE_CODE = b.ARTICLE_CODE);
```
    * Output
```
SELECT 
UNIQ_ID_AABJ AS a.ARTICLE_CODE, 
UNIQ_ID_AABK AS a.ARTICLE_LABEL, 
UNIQ_ID_AABL AS a.CATEGORY, 
UNIQ_ID_AABM AS a.SALE_PRICE, 
UNIQ_ID_AABN AS a.FAMILY_NAME, 
UNIQ_ID_AABO AS a.FAMILY_CODE, 
UNIQ_ID_AABP AS b.ARTICLE_CODE, 
UNIQ_ID_AABQ AS COLOR_CODE, 
UNIQ_ID_AABR AS b.ARTICLE_LABEL, 
UNIQ_ID_AABS AS COLOR_LABEL, 
UNIQ_ID_AABT AS b.CATEGORY, 
UNIQ_ID_AABU AS b.SALE_PRICE, 
UNIQ_ID_AABV AS b.FAMILY_NAME, 
UNIQ_ID_AABW AS b.FAMILY_CODE
 FROM (SELECT 
	UNIQ_ID_AAAV AS UNIQ_ID_AABJ,
	UNIQ_ID_AAAW AS UNIQ_ID_AABK,
	UNIQ_ID_AAAX AS UNIQ_ID_AABL,
	UNIQ_ID_AAAY AS UNIQ_ID_AABM,
	UNIQ_ID_AAAZ AS UNIQ_ID_AABN,
	UNIQ_ID_AABA AS UNIQ_ID_AABO,
	UNIQ_ID_AABB AS UNIQ_ID_AABP,
	UNIQ_ID_AABC AS UNIQ_ID_AABQ,
	UNIQ_ID_AABD AS UNIQ_ID_AABR,
	UNIQ_ID_AABE AS UNIQ_ID_AABS,
	UNIQ_ID_AABF AS UNIQ_ID_AABT,
	UNIQ_ID_AABG AS UNIQ_ID_AABU,
	UNIQ_ID_AABH AS UNIQ_ID_AABV,
	UNIQ_ID_AABI AS UNIQ_ID_AABW
 FROM 
	(SELECT 
		UNIQ_ID_AAAB AS UNIQ_ID_AAAV,
		UNIQ_ID_AAAC AS UNIQ_ID_AAAW,
		UNIQ_ID_AAAD AS UNIQ_ID_AAAX,
		UNIQ_ID_AAAE AS UNIQ_ID_AAAY,
		UNIQ_ID_AAAF AS UNIQ_ID_AAAZ,
		UNIQ_ID_AAAG AS UNIQ_ID_AABA,
		UNIQ_ID_AAAJ AS UNIQ_ID_AABB,
		UNIQ_ID_AAAK AS UNIQ_ID_AABC,
		UNIQ_ID_AAAL AS UNIQ_ID_AABD,
		UNIQ_ID_AAAM AS UNIQ_ID_AABE,
		UNIQ_ID_AAAN AS UNIQ_ID_AABF,
		UNIQ_ID_AAAO AS UNIQ_ID_AABG,
		UNIQ_ID_AAAP AS UNIQ_ID_AABH,
		UNIQ_ID_AAAQ AS UNIQ_ID_AABI
	 FROM 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AAAB,
				ARTICLE_LABEL AS UNIQ_ID_AAAC,
				CATEGORY AS UNIQ_ID_AAAD,
				SALE_PRICE AS UNIQ_ID_AAAE,
				FAMILY_NAME AS UNIQ_ID_AAAF,
				FAMILY_CODE AS UNIQ_ID_AAAG
			 FROM 
					efashion.ARTICLE_LOOKUP) a

				 JOIN 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AAAJ,
				COLOR_CODE AS UNIQ_ID_AAAK,
				ARTICLE_LABEL AS UNIQ_ID_AAAL,
				COLOR_LABEL AS UNIQ_ID_AAAM,
				CATEGORY AS UNIQ_ID_AAAN,
				SALE_PRICE AS UNIQ_ID_AAAO,
				FAMILY_NAME AS UNIQ_ID_AAAP,
				FAMILY_CODE AS UNIQ_ID_AAAQ
			 FROM 
					efashion.ARTICLE_COLOR_LOOKUP) b

				ON (a.UNIQ_ID_AAAB=b.UNIQ_ID_AAAJ)
) UNIQ_ID_AAAU
)
```
  1. Resolving jokers from sequential join
    * Input
```
Select * from efashion.ARTICLE_LOOKUP_CRITERIA alc 
    JOIN  efashion.ARTICLE_LOOKUP al ON (alc.ARTICLE_CODE = al.ARTICLE_CODE )
    JOIN efashion.ARTICLE_COLOR_LOOKUP acl ON alc.ARTICLE_CODE = acl.ARTICLE_CODE
    limit 10
```
    * Output
```
SELECT 
UNIQ_ID_AACM AS ID, 
UNIQ_ID_AACN AS alc.ARTICLE_CODE, 
UNIQ_ID_AACO AS CRITERIA_TYPE, 
UNIQ_ID_AACP AS CRITERIA, 
UNIQ_ID_AACQ AS CRITERIA_TYPE_LABEL, 
UNIQ_ID_AACR AS CRITERIA_LABEL, 
UNIQ_ID_AACS AS al.ARTICLE_CODE, 
UNIQ_ID_AACT AS UNIQ_ID_AAAS.ARTICLE_LABEL, 
UNIQ_ID_AACU AS UNIQ_ID_AAAS.CATEGORY, 
UNIQ_ID_AACV AS UNIQ_ID_AAAS.SALE_PRICE, 
UNIQ_ID_AACW AS UNIQ_ID_AAAS.FAMILY_NAME, 
UNIQ_ID_AACX AS UNIQ_ID_AAAS.FAMILY_CODE, 
UNIQ_ID_AACY AS acl.ARTICLE_CODE, 
UNIQ_ID_AACZ AS COLOR_CODE, 
UNIQ_ID_AADA AS acl.ARTICLE_LABEL, 
UNIQ_ID_AADB AS COLOR_LABEL, 
UNIQ_ID_AADC AS acl.CATEGORY, 
UNIQ_ID_AADD AS acl.SALE_PRICE, 
UNIQ_ID_AADE AS acl.FAMILY_NAME, 
UNIQ_ID_AADF AS acl.FAMILY_CODE
 FROM (SELECT 
	UNIQ_ID_AABS AS UNIQ_ID_AACM,
	UNIQ_ID_AABT AS UNIQ_ID_AACN,
	UNIQ_ID_AABU AS UNIQ_ID_AACO,
	UNIQ_ID_AABV AS UNIQ_ID_AACP,
	UNIQ_ID_AABW AS UNIQ_ID_AACQ,
	UNIQ_ID_AABX AS UNIQ_ID_AACR,
	UNIQ_ID_AABY AS UNIQ_ID_AACS,
	UNIQ_ID_AABZ AS UNIQ_ID_AACT,
	UNIQ_ID_AACA AS UNIQ_ID_AACU,
	UNIQ_ID_AACB AS UNIQ_ID_AACV,
	UNIQ_ID_AACC AS UNIQ_ID_AACW,
	UNIQ_ID_AACD AS UNIQ_ID_AACX,
	UNIQ_ID_AACE AS UNIQ_ID_AACY,
	UNIQ_ID_AACF AS UNIQ_ID_AACZ,
	UNIQ_ID_AACG AS UNIQ_ID_AADA,
	UNIQ_ID_AACH AS UNIQ_ID_AADB,
	UNIQ_ID_AACI AS UNIQ_ID_AADC,
	UNIQ_ID_AACJ AS UNIQ_ID_AADD,
	UNIQ_ID_AACK AS UNIQ_ID_AADE,
	UNIQ_ID_AACL AS UNIQ_ID_AADF
 FROM 
	(SELECT 
		UNIQ_ID_AAAT AS UNIQ_ID_AABS,
		UNIQ_ID_AAAU AS UNIQ_ID_AABT,
		UNIQ_ID_AAAV AS UNIQ_ID_AABU,
		UNIQ_ID_AAAW AS UNIQ_ID_AABV,
		UNIQ_ID_AAAX AS UNIQ_ID_AABW,
		UNIQ_ID_AAAY AS UNIQ_ID_AABX,
		UNIQ_ID_AAAZ AS UNIQ_ID_AABY,
		UNIQ_ID_AABA AS UNIQ_ID_AABZ,
		UNIQ_ID_AABB AS UNIQ_ID_AACA,
		UNIQ_ID_AABC AS UNIQ_ID_AACB,
		UNIQ_ID_AABD AS UNIQ_ID_AACC,
		UNIQ_ID_AABE AS UNIQ_ID_AACD,
		UNIQ_ID_AABG AS UNIQ_ID_AACE,
		UNIQ_ID_AABH AS UNIQ_ID_AACF,
		UNIQ_ID_AABI AS UNIQ_ID_AACG,
		UNIQ_ID_AABJ AS UNIQ_ID_AACH,
		UNIQ_ID_AABK AS UNIQ_ID_AACI,
		UNIQ_ID_AABL AS UNIQ_ID_AACJ,
		UNIQ_ID_AABM AS UNIQ_ID_AACK,
		UNIQ_ID_AABN AS UNIQ_ID_AACL
	 FROM 
			(SELECT 
				UNIQ_ID_AAAB AS UNIQ_ID_AAAT,
				UNIQ_ID_AAAC AS UNIQ_ID_AAAU,
				UNIQ_ID_AAAD AS UNIQ_ID_AAAV,
				UNIQ_ID_AAAE AS UNIQ_ID_AAAW,
				UNIQ_ID_AAAF AS UNIQ_ID_AAAX,
				UNIQ_ID_AAAG AS UNIQ_ID_AAAY,
				UNIQ_ID_AAAJ AS UNIQ_ID_AAAZ,
				UNIQ_ID_AAAK AS UNIQ_ID_AABA,
				UNIQ_ID_AAAL AS UNIQ_ID_AABB,
				UNIQ_ID_AAAM AS UNIQ_ID_AABC,
				UNIQ_ID_AAAN AS UNIQ_ID_AABD,
				UNIQ_ID_AAAO AS UNIQ_ID_AABE
			 FROM 
					(SELECT 
						ID AS UNIQ_ID_AAAB,
						ARTICLE_CODE AS UNIQ_ID_AAAC,
						CRITERIA_TYPE AS UNIQ_ID_AAAD,
						CRITERIA AS UNIQ_ID_AAAE,
						CRITERIA_TYPE_LABEL AS UNIQ_ID_AAAF,
						CRITERIA_LABEL AS UNIQ_ID_AAAG
					 FROM 
							efashion.ARTICLE_LOOKUP_CRITERIA) alc

						 JOIN 
					(SELECT 
						ARTICLE_CODE AS UNIQ_ID_AAAJ,
						ARTICLE_LABEL AS UNIQ_ID_AAAK,
						CATEGORY AS UNIQ_ID_AAAL,
						SALE_PRICE AS UNIQ_ID_AAAM,
						FAMILY_NAME AS UNIQ_ID_AAAN,
						FAMILY_CODE AS UNIQ_ID_AAAO
					 FROM 
							efashion.ARTICLE_LOOKUP) al

						ON (alc.UNIQ_ID_AAAC=al.UNIQ_ID_AAAJ)
) UNIQ_ID_AAAS

				 JOIN 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AABG,
				COLOR_CODE AS UNIQ_ID_AABH,
				ARTICLE_LABEL AS UNIQ_ID_AABI,
				COLOR_LABEL AS UNIQ_ID_AABJ,
				CATEGORY AS UNIQ_ID_AABK,
				SALE_PRICE AS UNIQ_ID_AABL,
				FAMILY_NAME AS UNIQ_ID_AABM,
				FAMILY_CODE AS UNIQ_ID_AABN
			 FROM 
					efashion.ARTICLE_COLOR_LOOKUP) acl

				ON (UNIQ_ID_AAAS.UNIQ_ID_AAAU=acl.UNIQ_ID_AABG)
) UNIQ_ID_AABR 

LIMIT 10)
```
  1. Transforming _WHERE_ into join with Condition on `a.SALE_PRICE`
    * Input
```
SELECT a.SALE_PRICE,a.ARTICLE_CODE,b.COLOR_LABEL,b.CATEGORY 
FROM efashion.ARTICLE_LOOKUP a, efashion.ARTICLE_COLOR_LOOKUP b 
WHERE (a.ARTICLE_CODE = b.ARTICLE_CODE) AND (a.SALE_PRICE >= 100);
```
    * Output
```
SELECT 
UNIQ_ID_AABK AS a.SALE_PRICE, 
UNIQ_ID_AABL AS a.ARTICLE_CODE, 
UNIQ_ID_AABM AS b.COLOR_LABEL, 
UNIQ_ID_AABN AS b.CATEGORY
 FROM (SELECT 
	UNIQ_ID_AAAZ AS UNIQ_ID_AABK,
	UNIQ_ID_AAAW AS UNIQ_ID_AABL,
	UNIQ_ID_AABF AS UNIQ_ID_AABM,
	UNIQ_ID_AABG AS UNIQ_ID_AABN
 FROM 
	(SELECT 
		UNIQ_ID_AAAB AS UNIQ_ID_AAAW,
		UNIQ_ID_AAAE AS UNIQ_ID_AAAZ,
		UNIQ_ID_AAAM AS UNIQ_ID_AABF,
		UNIQ_ID_AAAN AS UNIQ_ID_AABG
	 FROM 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AAAB,
				ARTICLE_LABEL AS UNIQ_ID_AAAC,
				CATEGORY AS UNIQ_ID_AAAD,
				SALE_PRICE AS UNIQ_ID_AAAE,
				FAMILY_NAME AS UNIQ_ID_AAAF,
				FAMILY_CODE AS UNIQ_ID_AAAG
			 FROM 
					efashion.ARTICLE_LOOKUP) a

				 JOIN 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AAAJ,
				COLOR_CODE AS UNIQ_ID_AAAK,
				ARTICLE_LABEL AS UNIQ_ID_AAAL,
				COLOR_LABEL AS UNIQ_ID_AAAM,
				CATEGORY AS UNIQ_ID_AAAN,
				SALE_PRICE AS UNIQ_ID_AAAO,
				FAMILY_NAME AS UNIQ_ID_AAAP,
				FAMILY_CODE AS UNIQ_ID_AAAQ
			 FROM 
					efashion.ARTICLE_COLOR_LOOKUP) b

				ON (a.UNIQ_ID_AAAB=b.UNIQ_ID_AAAJ)

WHERE a.UNIQ_ID_AAAE>=100) UNIQ_ID_AAAV
)
```
  1. Here we use BigQuery's capability to UNIONs, by unioning the two sides of the _OR_
    * Input
```
SELECT a.SALE_PRICE,a.ARTICLE_CODE,b.COLOR_LABEL,b.CATEGORY 
FROM efashion.ARTICLE_LOOKUP a, efashion.ARTICLE_COLOR_LOOKUP b 
WHERE (a.ARTICLE_CODE = b.ARTICLE_CODE) AND ((a.SALE_PRICE <= 100) OR (a.SALE_PRICE  >= 90))
```
    * Output
```
SELECT 
UNIQ_ID_AACA AS a.SALE_PRICE, 
UNIQ_ID_AACB AS a.ARTICLE_CODE, 
UNIQ_ID_AACC AS b.COLOR_LABEL, 
UNIQ_ID_AACD AS b.CATEGORY
 FROM (SELECT 
	UNIQ_ID_AABA AS UNIQ_ID_AACA,
	UNIQ_ID_AAAX AS UNIQ_ID_AACB,
	UNIQ_ID_AABG AS UNIQ_ID_AACC,
	UNIQ_ID_AABH AS UNIQ_ID_AACD
 FROM 
	(SELECT 
		UNIQ_ID_AAAB AS UNIQ_ID_AAAX,
		UNIQ_ID_AAAE AS UNIQ_ID_AABA,
		UNIQ_ID_AAAM AS UNIQ_ID_AABG,
		UNIQ_ID_AAAN AS UNIQ_ID_AABH
	 FROM 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AAAB,
				ARTICLE_LABEL AS UNIQ_ID_AAAC,
				CATEGORY AS UNIQ_ID_AAAD,
				SALE_PRICE AS UNIQ_ID_AAAE,
				FAMILY_NAME AS UNIQ_ID_AAAF,
				FAMILY_CODE AS UNIQ_ID_AAAG
			 FROM 
					efashion.ARTICLE_LOOKUP) a

				 JOIN 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AAAJ,
				COLOR_CODE AS UNIQ_ID_AAAK,
				ARTICLE_LABEL AS UNIQ_ID_AAAL,
				COLOR_LABEL AS UNIQ_ID_AAAM,
				CATEGORY AS UNIQ_ID_AAAN,
				SALE_PRICE AS UNIQ_ID_AAAO,
				FAMILY_NAME AS UNIQ_ID_AAAP,
				FAMILY_CODE AS UNIQ_ID_AAAQ
			 FROM 
					efashion.ARTICLE_COLOR_LOOKUP) b

				ON (a.UNIQ_ID_AAAB=b.UNIQ_ID_AAAJ)

WHERE a.UNIQ_ID_AAAE<=100) UNIQ_ID_AAAW
,	(SELECT 
		UNIQ_ID_AAAB AS UNIQ_ID_AAAX,
		UNIQ_ID_AAAE AS UNIQ_ID_AABA,
		UNIQ_ID_AAAM AS UNIQ_ID_AABG,
		UNIQ_ID_AAAN AS UNIQ_ID_AABH
	 FROM 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AAAB,
				ARTICLE_LABEL AS UNIQ_ID_AAAC,
				CATEGORY AS UNIQ_ID_AAAD,
				SALE_PRICE AS UNIQ_ID_AAAE,
				FAMILY_NAME AS UNIQ_ID_AAAF,
				FAMILY_CODE AS UNIQ_ID_AAAG
			 FROM 
					efashion.ARTICLE_LOOKUP) a

				 JOIN 
			(SELECT 
				ARTICLE_CODE AS UNIQ_ID_AAAJ,
				COLOR_CODE AS UNIQ_ID_AAAK,
				ARTICLE_LABEL AS UNIQ_ID_AAAL,
				COLOR_LABEL AS UNIQ_ID_AAAM,
				CATEGORY AS UNIQ_ID_AAAN,
				SALE_PRICE AS UNIQ_ID_AAAO,
				FAMILY_NAME AS UNIQ_ID_AAAP,
				FAMILY_CODE AS UNIQ_ID_AAAQ
			 FROM 
					efashion.ARTICLE_COLOR_LOOKUP) b

				ON (a.UNIQ_ID_AAAB=b.UNIQ_ID_AAAJ)
WHERE a.UNIQ_ID_AAAE>=90) UNIQ_ID_AABL
)
```
  1. Passing back only the columns appearing in subqueries when selecting `*`:
    * Input
```
SELECT * 
from 
	(SELECT COLOR_LABEL,CATEGORY one,CATEGORY two 
	FROM efashion.ARTICLE_COLOR_LOOKUP)
```
    * Output
```
SELECT 
UNIQ_ID_AAAO AS COLOR_LABEL, 
UNIQ_ID_AAAP AS one, 
UNIQ_ID_AAAQ AS two
 FROM (SELECT 
	UNIQ_ID_AAAL AS UNIQ_ID_AAAO,
	UNIQ_ID_AAAM AS UNIQ_ID_AAAP,
	UNIQ_ID_AAAN AS UNIQ_ID_AAAQ
 FROM 
	(SELECT 
		UNIQ_ID_AAAF AS UNIQ_ID_AAAL,
		UNIQ_ID_AAAG AS UNIQ_ID_AAAM,
		UNIQ_ID_AAAG AS UNIQ_ID_AAAN
	 FROM 
		(SELECT 
			ARTICLE_CODE AS UNIQ_ID_AAAC,
			COLOR_CODE AS UNIQ_ID_AAAD,
			ARTICLE_LABEL AS UNIQ_ID_AAAE,
			COLOR_LABEL AS UNIQ_ID_AAAF,
			CATEGORY AS UNIQ_ID_AAAG,
			SALE_PRICE AS UNIQ_ID_AAAH,
			FAMILY_NAME AS UNIQ_ID_AAAI,
			FAMILY_CODE AS UNIQ_ID_AAAJ
		 FROM 
				efashion.ARTICLE_COLOR_LOOKUP) efashion.ARTICLE_COLOR_LOOKUP
) UNIQ_ID_AAAA
)
```