package api.gdax.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private final String id;
    private final String baseCurrency;
    private final String quoteCurrency;
    private final BigDecimal baseMinSize;
    private final BigDecimal baseMaxSize;
    private final BigDecimal quoteIncrement;

    public Product(@JsonProperty("id") String id,
                   @JsonProperty("base_currency") String baseCurrency,
                   @JsonProperty("quote_currency") String quoteCurrency,
                   @JsonProperty("base_min_size") BigDecimal baseMinSize,
                   @JsonProperty("base_max_size") BigDecimal baseMaxSize,
                   @JsonProperty("quote_increment") BigDecimal quoteIncrement) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.baseMinSize = baseMinSize;
        this.baseMaxSize = baseMaxSize;
        this.quoteIncrement = quoteIncrement;
    }

    public String getId() {
        return id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public BigDecimal getBaseMinSize() {
        return baseMinSize;
    }

    public BigDecimal getBaseMaxSize() {
        return baseMaxSize;
    }

    public BigDecimal getQuoteIncrement() {
        return quoteIncrement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(baseCurrency, product.baseCurrency) &&
                Objects.equals(quoteCurrency, product.quoteCurrency) &&
                Objects.equals(baseMinSize, product.baseMinSize) &&
                Objects.equals(baseMaxSize, product.baseMaxSize) &&
                Objects.equals(quoteIncrement, product.quoteIncrement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrency, quoteCurrency, baseMinSize, baseMaxSize, quoteIncrement);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Product{");
        sb.append("id='").append(id).append('\'');
        sb.append(", baseCurrency='").append(baseCurrency).append('\'');
        sb.append(", quoteCurrency='").append(quoteCurrency).append('\'');
        sb.append(", baseMinSize=").append(baseMinSize);
        sb.append(", baseMaxSize=").append(baseMaxSize);
        sb.append(", quoteIncrement=").append(quoteIncrement);
        sb.append('}');
        return sb.toString();
    }
}
