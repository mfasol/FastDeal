CREATE OR REPLACE FORCE VIEW PUBLIC.V_SALES_BY_PRICE_ES(PRODUCT_KEY, SALES, PROFIT, NUM_OF_SALES) AS
  SELECT
    _0.PRODUCT_KEY,
    _0.SALES,
    _0.PROFIT,
    _0.NUM_OF_SALES
  FROM (
         SELECT
           PRODUCT_KEY,
           ROUND(SALES, 2) AS SALES,
           PROFIT,
           COUNT(ITEM_ID)  AS NUM_OF_SALES
         FROM PUBLIC.V_SALES_CONTRIBUTION
         WHERE (TYPE = 'SALE')
               AND ((SALES > 0)
                    AND ((SALES_COUNTRY = 'ES')
                         AND (COUNTER = 1)))
         GROUP BY PRODUCT_KEY, ROUND(SALES, 2), PROFIT
         ORDER BY 1, 2 DESC
       ) _0
  WHERE NUM_OF_SALES > 1