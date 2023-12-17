package TDD_Lab;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class InstockTest {
    private static final Product[] PRODUCTS_ARRAY =
            {new Product("Tester1", 11.44, 5),
                    new Product("Tester5", 114.99, 1),
                    new Product("Tester3", 21.04, 99),
                    new Product("Tester4", 5.3, 5),
                    new Product("Tester2", 1.24, 10)
            };

    private ProductStock inStockProducts;

    private List<Product> fillProductsInStockAndReturnProductsList(ProductStock stock) {
        List<Product> products = Arrays.asList(PRODUCTS_ARRAY);

        for (Product p : products) {
            stock.add(p);
        }
        return products;
    }

    private static <T> List<T> fillListOfProducts(Iterable<T> products) {
        Assert.assertNotNull(products);
        List<T> productsList = new ArrayList<>();

        for(T p : products) {
            productsList.add(p);
        }

        return productsList;
    }

    @Before
    public void start() {
        this.inStockProducts = new Instock();
    }

    @Test
    public void testIfProductIsAddedCorrectly() {

        Product product = new Product("Tester", 11.56, 3);
        ProductStock instock = new Instock();

        Assert.assertFalse(instock.contains(product));
        instock.add(product);
        Assert.assertTrue(instock.contains(product));

    }

    @Test
    public void testGetCountMethodAndShouldReturnCorrectCountInstockItems() {

        List<Product> currentProducts = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        Assert.assertNotNull(this.inStockProducts);
        Assert.assertEquals(currentProducts.size(), inStockProducts.getCount());

    }

    @Test
    public void testFindMethodShouldReturnNthProductAddedInStockBasedOnInsertionOrder() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        int index = products.size() - 3;

        Assert.assertEquals(products.get(index), this.inStockProducts.find(index));
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void findMethodShouldThrowExceptionWhenNoSuchPositiveIndex() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);
        this.inStockProducts.find(43);
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void findMethodShouldThrowExceptionWhenIndexIsNegativeNumber() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);
        this.inStockProducts.find(-1);
    }

    @Test
    public void quantityOfAGivenProductShouldBeChanged() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);
        Product product = products.get(2);
        int newQuantity = product.getQuantity() + 1;

        this.inStockProducts.changeQuantity(product.getLabel(), newQuantity);
        Assert.assertEquals(newQuantity, product.getQuantity());
    }

    @Test (expected = IllegalArgumentException.class)
    public void changeQuantityMethodShouldThrowExceptionIfProductIsNotInStock() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        this.inStockProducts.changeQuantity("productNotInStock", 15);
    }

    @Test
    public void testFindByLabelMethodShouldReturnProduct() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);
        Product testProduct = products.get(1);

        Product resultProduct = this.inStockProducts.findByLabel(testProduct.getLabel());
        Assert.assertNotNull(resultProduct);

        Assert.assertEquals(testProduct, resultProduct);
    }

    @Test (expected = IllegalArgumentException.class)
    public void findByLabelMethodShouldThrowExceptionIfNoSuchProductInStock() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        this.inStockProducts.findByLabel("NoSuchProductInStock");
    }

    @Test
    public void testShouldReturnFirstNElementsOrderedInAlphabeticalOrder() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        int itemsNumber = 4;

        List<String> expectedProducts = products.stream()
                .map(Product::getLabel)
                .sorted()
                .limit(itemsNumber)
                .collect(Collectors.toList());

        Iterable<Product> sortedProducts =
                this.inStockProducts.findFirstByAlphabeticalOrder(itemsNumber);

        Assert.assertNotNull(sortedProducts);

        List<String> actualProducts = new ArrayList<>();

        for (Product p : sortedProducts) {
            actualProducts.add(p.getLabel());
        }

        Assert.assertEquals("Products count is not the same as required",
                itemsNumber, actualProducts.size());

        Assert.assertEquals("Products are not sorted correctly",
                expectedProducts, actualProducts);
    }

    @Test
    public void testShouldReturnEmptyCollectionBecausePassedArgumentIsOutOfBounds() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        int numberOutOfBound = 16;

        Iterable<Product> sortedProducts =
                this.inStockProducts.findFirstByAlphabeticalOrder(numberOutOfBound);

        List<Product> actualProducts = fillListOfProducts(sortedProducts);

        Assert.assertTrue(actualProducts.isEmpty());
    }

    @Test
    public void testShouldFindAllInRangeProductsLowerIsExclusiveHigherIsInclusiveAndReturnInDescendingOrder() {
        double lowerEnd = 5.3;
        double higherEnd = 98.99;

        List<Product> expectedSorting = fillProductsInStockAndReturnProductsList(this.inStockProducts).stream()
                .filter(p -> p.getPrice() > lowerEnd && p.getPrice() <= higherEnd)
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .collect(Collectors.toList());

        Iterable<Product> itemsInRange = this.inStockProducts.findAllInRange(lowerEnd, higherEnd);

        List<Product> actualSorting = fillListOfProducts(itemsInRange);

        Assert.assertEquals(expectedSorting.size(), actualSorting.size());
        Assert.assertEquals(expectedSorting, actualSorting);
    }

    @Test
    public void testShouldReturnEmptyCollectionIfNoProductsWithinGivenPriceRange() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        double lowerEnd = 230.30;
        double higherEnd = 439.45;

        Iterable<Product> itemsInPriceRange = this.inStockProducts.findAllInRange(lowerEnd, higherEnd);

        List<Product> actualProducts = fillListOfProducts(itemsInPriceRange);

        Assert.assertTrue(actualProducts.isEmpty());
    }

    @Test
    public void testShouldReturnAllProductsEqualToGivenPrice() {
        double givenPrice = 5.3;

        List<Product> expectedProducts = fillProductsInStockAndReturnProductsList(this.inStockProducts)
                .stream()
                .filter(p -> p.getPrice() == givenPrice)
                .collect(Collectors.toList());

        Iterable<Product> matchedProducts = this.inStockProducts.findAllByPrice(givenPrice);

        List<Product> actualProducts = fillListOfProducts(matchedProducts);

        Assert.assertEquals(expectedProducts.size(), actualProducts.size());
        Assert.assertEquals(expectedProducts, actualProducts);
    }

    @Test
    public void testShouldReturnEmptyCollectionWhenProductDoNotMatchByPrice() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        double givenPrice = 543.66;

        Iterable<Product> matchedProducts = this.inStockProducts.findAllByPrice(givenPrice);

        List<Product> actualProduct = fillListOfProducts(matchedProducts);

        Assert.assertTrue(actualProduct.isEmpty());
    }

    @Test
    public void testShouldReturnFirstMostExpensiveProducts() {
        int productsToFind = 5;

        List<Product> expectedProducts = fillProductsInStockAndReturnProductsList(this.inStockProducts)
                .stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .limit(productsToFind)
                .collect(Collectors.toList());

        Iterable<Product> filteredProducts = this.inStockProducts.findFirstMostExpensiveProducts(productsToFind);

        List<Product> actualProducts = fillListOfProducts(filteredProducts);

        Assert.assertEquals(expectedProducts, actualProducts);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testShouldThrowExceptionIfActualElementsAreLessThanRequired() {
        List<Product> products = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        int productsToFind = products.size() + 3;

        Iterable<Product> filteredProducts = this.inStockProducts.findFirstMostExpensiveProducts(productsToFind);
    }

    @Test
    public void testShouldReturnAllProductsMatchedByGivenQuantity() {
        int givenQuantity = 5;

        List<Product> expectedProducts = fillProductsInStockAndReturnProductsList(this.inStockProducts)
                .stream()
                .filter(p -> p.getQuantity() == givenQuantity)
                .collect(Collectors.toList());

        Iterable<Product> products = this.inStockProducts.findAllByQuantity(givenQuantity);

        List<Product> actualProducts = fillListOfProducts(products);

        Assert.assertEquals(expectedProducts, actualProducts);
    }

    @Test
    public void testShouldReturnEmptyCollectionWhenNoProductsMatchedByGivenQuantity() {
        int givenQuantity = 1002;

        Iterable<Product> products = this.inStockProducts.findAllByQuantity(givenQuantity);
        List<Product> actualProducts = fillListOfProducts(products);

        Assert.assertTrue(actualProducts.isEmpty());
    }

    @Test
    public void testShouldReturnAllProductsInStock() {
        List<Product> expectedProducts = fillProductsInStockAndReturnProductsList(this.inStockProducts);

        Iterator<Product> products = this.inStockProducts.iterator();
        Assert.assertNotNull(products);

        List<Product> actualProducts = new ArrayList<>();

        while(products.hasNext()) {
            actualProducts.add(products.next());
        }

        Assert.assertEquals("Products do not match", expectedProducts.size(), actualProducts.size());


    }
}
