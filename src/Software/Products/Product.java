package Software.Products;

import Software.Importable;

/**
 * Created by Michele on 16/10/2015.
 */
public class Product extends Importable{

    private String asin = null;
    private String ean = null;
    private String sellerSku = null;
    private String itemName = null;
    private String manufacturer = null;
    private String openDate = null;
    private String status = null;

    private String productKey = null;

    public Product(String asin, String ean, String sellerSku, String itemName,String manufacturer, String openDate)
    {
        this.asin = asin;
        this.ean = ean;
        this.sellerSku = sellerSku;
        this.itemName = itemName;
        this.manufacturer = manufacturer;
        this.openDate  = openDate;
        productKey = asin + ean + sellerSku;
    }

    public String getAsin()
    {
        return asin;
    }

    public void setAsin(String asin)
    {
        this.asin = asin;
    }

    public String getEan()
    {
        return ean;
    }

    public void setEan(String ean)
    {
        this.ean = ean;
    }

    public String getSellerSku()
    {
        return sellerSku;
    }

    public void setSellerSku(String sellerSku)
    {
        this.sellerSku = sellerSku;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    public String getOpenDate()
    {
        return openDate;
    }

    public void setOpenDate(String openDate)
    {
        this.openDate = openDate;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getProductKey()
    {
        return productKey;
    }

    @Override
    public String toString()
    {
        return "Product{" +
                "asin='" + asin + '\'' +
                ", ean='" + ean + '\'' +
                ", sellerSku='" + sellerSku + '\'' +
                ", itemName='" + itemName + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", openDate='" + openDate + '\'' +
                ", status='" + status + '\'' +
                ", productKey='" + productKey + '\'' +
                '}';
    }

    public Product(String asin) {
        DbManagerProducts dbManagerProducts = new DbManagerProducts();
        dbManagerProducts.retrieveTarget(asin);
    }

    public boolean equals(Product otherProduct)
    {
        if (this == otherProduct) return true;
        if (!asin.equals(otherProduct.asin)) return false;
        if (!ean.equals(otherProduct.ean)) return false;
        return sellerSku.equals(otherProduct.sellerSku);
    }

    @Override
    public int hashCode()
    {
        int result = asin.hashCode();
        result = 31 * result + ean.hashCode();
        result = 31 * result + sellerSku.hashCode();
        return result;
    }
}
