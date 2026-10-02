## Known limitations

1. **Most values are hard coded.** Discount tiers, percentages, delivery fees and the tax rate are constants in `OrderPricingService`. Changing a rule means editing the code. Next step: read them from a config file.

2. **No currency conversion.** Everything is in naira (NGN), including the international delivery fee. The program has no exchange rates. Next step: store a currency with each order and convert using a rate source.