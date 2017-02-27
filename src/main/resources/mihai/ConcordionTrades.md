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

## [Example 3](-)
Match a Trade and a CCP trade
###Trade definition:

|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|------------------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                                   |EX997                |2017-01-01    |IBM     |BUY          |100         |EUR         |200       |


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
|--------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                         |EX99                  |2017-01-01    |IBM     |BUY          |100         |EUR         |202       |


###CCP Trade definition:

|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|------------|-----------------|----------------|----------------|--------------|
|EX99                                                    |2017-01-01        |IBM         |BUY              |100             |EUR	         |202           |


### Fully Match a Trade 
|[Execute Full Match][]|[Full match message](- "?=#fullMatch")|
|--------------------|--------------------------------|
|                    | Full match                     |



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


 [Execute Full Match]: - "#fullMatch = matchingTest()"


## [Scenario 3](-)
Match a Trade within tolerance for amount
  
###Trade definition:
  
|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|--------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                           |EX99                  |2017-01-01    |IBM     |BUY          |100         |EUR         |202       |
  
  
###CCP Trade definition:
  
|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|------------|-----------------|----------------|----------------|--------------|
|EX99                                                    |2017-01-01        |IBM         |BUY              |100             |EUR	         |205           |
  
  
### Match within tolerance for amount 
|[Execute within tolerance][]|[Within tolerance match message](- "?=#withinMatch")|
|----------------------------|----------------------------------------------------|
|                            | Match within tolerance for amount                  |
  
  
  
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
  
  
 [Execute within tolerance]: - "#withinMatch = matchingTest()"
 
## [Scenario 4](-)
Unmatch economics
   
###Trade definition:
   
|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|--------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                         |EX99                  |2017-01-01    |APPL    |BUY          |100         |EUR         |202       |
   
###CCP Trade definition:
   
|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|------------|-----------------|----------------|----------------|--------------|
|EX99                                                    |2017-01-01        |IBM         |BUY              |100             |EUR	         |202           |
   
   
### Economics mismatch 
|[Execute economics mismatch][]|[Economics mismatch](- "?=#economicsMismatch")|
|-------------------------------|----------------------------------------------------|
|                               | Economics mismatch                 |
   
   
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
   
   
[Execute economics mismatch]: - "#economicsMismatch = matchingTest()"

## [Scenario 5](-)
Unmatch outside of tolerance for amount
   
###Trade definition:
   
|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|--------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                         |EX99                  |2017-01-01    |IBM     |BUY          |100         |EUR         |202       |
   
###CCP Trade definition:
   
|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|------------|-----------------|----------------|----------------|--------------|
|EX99                                                    |2017-01-01        |IBM         |BUY              |100             |EUR	         |265           |
   
   
### Unmatch outside of tolerance for amount 
|[Execute outside of tolerance for amount][]|[Outside of tolerance](- "?=#outsideTolerance")|
|-------------------------------------------|----------------------------------------------------|
|                                           |Outside of tolerance for amount                    |
   
   
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
   
   
[Execute outside of tolerance for amount]: - "#outsideTolerance = matchingTest()"

## [Scenario 6](-)
Ccp trade unmatch
   
### Trade definition:
   
|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|--------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                         |EX99                  |2017-01-01    |IBM     |BUY          |100         |EUR         |202       |
   
### CCP Trade definition:
   
|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|------------|-----------------|----------------|----------------|--------------|
|no ccp entry                                            |2017-01-01        |IBM         |BUY              |100             |EUR	         |202           |
   
   
### CCP trade unmatch
|[Execute ccp unmatch][]|[Missing ccp](- "?=#ccpUnmatch")|
|-----------------------|--------------------------------|
|                       |Missing CCP trade             |
   
   
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
   
   
[Execute ccp unmatch]: - "#ccpUnmatch = matchingTest()"

## [Scenario 7](-)
Match a Trade and a CCP trade

### Trade definition:

|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|------------------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                                   |EX997                 |2017-01-01    |IBM     |BUY          |100         |EUR         |200       |


### CCP Trade definition:

|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|-----------|-----------------|----------------|----------------|---------------|
|EX997                                                   |2017-01-01        |IBM        |BUY              |100             |EUR	            |200            |


### Matched CCP
|[Execute ccp match][]|[Match ccp](- "?=#ccpmatch")|
|---------------------|----------------------------|
|                     |Full match                  |

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


[Execute ccp match]: - "#ccpmatch = matchingTest()"

## [Scenario 8](-)
Unmatch CCP economics
   
### Trade definition:
   
|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|--------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                         |EX99                  |2017-01-01    |APPL    |BUY          |100         |EUR         |202       |
   
### CCP Trade definition:
   
|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|------------|-----------------|----------------|----------------|--------------|
|EX99                                                    |2017-01-01        |IBM         |BUY              |100             |EUR	         |202           |
   
   
### Economics mismatch 
|[Execute ccp economics mismatch][]|[CCP Economics mismatch](- "?=#ccpeconomicsMismatch")|
|----------------------------------|-----------------------------------------------------|
|                                  | Economics mismatch                                  |
   
   
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
   
   
[Execute ccp economics mismatch]: - "#ccpeconomicsMismatch = matchingTest()"

## [Scenario 9](-)
Unmatch outside of tolerance for amount
   
### Trade definition:
   
|[Set Trade][][Trade Reference][]|[Exchange Reference][]|[Trade Date][]|[Isin][]|[Direction][]|[Quantity][]|[Currency][]|[Amount][]|
|--------------------------------|----------------------|--------------|--------|-------------|------------|------------|----------|
|T123	                         |EX99                  |2017-01-01    |IBM     |BUY          |100         |EUR         |202       |
   
### CCP Trade definition:
   
|[Set Exchange Reference CCP][][Exchange Reference CCP][]|[Trade Date CCP][]|[Isin CCP][]|[Direction CCP][]|[Quantity CCP][]|[Currency CCP][]|[Amount CCP][]|
|--------------------------------------------------------|------------------|------------|-----------------|----------------|----------------|--------------|
|EX99                                                    |2017-01-01        |IBM         |BUY              |100             |EUR	         |265           |
   
   
### Unmatch outside of tolerance for amount 
|[Execute ccp outside of tolerance for amount][]|[CCP Outside of tolerance](- "?=#ccpoutsideTolerance")|
|-----------------------------------------------|------------------------------------------------------|
|                                               |Outside of tolerance for amount                       |
   
   
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
   
   
[Execute ccp outside of tolerance for amount]: - "#ccpoutsideTolerance = matchingTest()"

