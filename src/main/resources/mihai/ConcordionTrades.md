# Add a trade in the traders list

Given a trade list
When a new trade is added
Then the new trade is present into the list

## [Example 1](-)

When [adding a trade](- "#num = canAddATrade()") to an existing trade list the number of trades is increased by [1](- "?=#num")

# Add a trade in the ccp list
Given a CPP list
When a new CPP trade is added
Then the new CPP trade is present into the list

## [Example 2](-)

When [adding a CPP trade](- "#num = canAddACcpTrade()") to an existing trade list the number of trades is increased by [1](- "?=#num")

# Matching scenarious

## [Scenario 1](-)
Match a Trade and a CCP trade
###Trade definition:

|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|------------------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                                   |EX9976                |2017-01-01    |IBM     |BUY          |100         |EUR         |200       |


###CCP Trade definition:
|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|-----------|-----------------|----------------|----------------|---------------|
|EX997                                                   |2017-01-01        |IBM        |BUY              |100             |EUR	            |200            |


### Matched Trade/CCP
|[Execute][] |[Matched Trade][]|[Matched CCP]|
|------------|-----------------|-------------|
|            |EX997            |EX997        | 



[Trade Reference]: - "#tradeReference"
[Set Trade]: - "setTradeData(#tradeReference,#exchangeReference,#tradeDate,#isin,#tradeDirection,#tradeQuantity,#tradeCurrency,#tradeAmount)"
[Exchange Reference]: - "#exchangeReference"
[Trade Date]: - "#tradeDate"
[Isin]: - "#isin"
[Direction]: - "#tradeDirection"
[Quantity]: - "#tradeQuantity"
[Currency]: - "#tradeCurrency"
[Amount]: - "#tradeAmount"


[Exchange Reference CCP]: - "#exchangeReferenceCcp"
[Set Exchange Reference CCP]: - "setCcpData(#exchangeReferenceCcp,#tradeDateCcp,#isinCcp,#tradeDirectionCcp,#tradeQuantityCcp,#tradeCurrencyCcp,#tradeAmountCcp)"
[Trade Date CCP]: - "#tradeDateCcp"
[Isin CCP]: - "#isinCcp"
[Direction CCP]: - "#tradeDirectionCcp"
[Quantity CCP]: - "#tradeQuantityCcp"
[Currency CCP]: - "#tradeCurrencyCcp"
[Amount CCP]: - "#tradeAmountCcp"


[Execute]: - "#result = canPerformAMatch()"
[Matched Trade]: - "?=#result.exchangeRef"
[Matched CCP]: - "?=#result.ccpExchangeRef"

## [Scenario 2](-)
Match a Trade and a CCP trade
###Trade definition:

|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|------------------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                                   |EX99                  |2017-01-01    |IBM     |BUY          |100         |EUR         |200       |


###CCP Trade definition:
|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|-----------|-----------------|----------------|----------------|---------------|
|EX99                                                    |2017-01-01        |IBM        |BUY              |100             |EUR	            |200            |


### Matched Trade/CCP
|[Execute][] |[Matched Trade][]|[Matched CCP]|
|------------|-----------------|-------------|
|            |EX99             |EX99         | 



[Trade Reference]: - "#tradeReference"
[Set Trade]: - "setTradeData(#tradeReference,#exchangeReference,#tradeDate,#isin,#tradeDirection,#tradeQuantity,#tradeCurrency,#tradeAmount)"
[Exchange Reference]: - "#exchangeReference"
[Trade Date]: - "#tradeDate"
[Isin]: - "#isin"
[Direction]: - "#tradeDirection"
[Quantity]: - "#tradeQuantity"
[Currency]: - "#tradeCurrency"
[Amount]: - "#tradeAmount"


[Exchange Reference CCP]: - "#exchangeReferenceCcp"
[Set Exchange Reference CCP]: - "setCcpData(#exchangeReferenceCcp,#tradeDateCcp,#isinCcp,#tradeDirectionCcp,#tradeQuantityCcp,#tradeCurrencyCcp,#tradeAmountCcp)"
[Trade Date CCP]: - "#tradeDateCcp"
[Isin CCP]: - "#isinCcp"
[Direction CCP]: - "#tradeDirectionCcp"
[Quantity CCP]: - "#tradeQuantityCcp"
[Currency CCP]: - "#tradeCurrencyCcp"
[Amount CCP]: - "#tradeAmountCcp"


[Execute]: - "#result = canPerformAMatch()"
[Matched Trade]: - "?=#result.exchangeRef"
[Matched CCP]: - "?=#result.ccpExchangeRef"

[Next test suit]: /akka-matcher-4/target/mihai/mihai/ConcordionTrades.html/ "Titlu"
[google]: http://google.com/        "Google"