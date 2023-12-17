package TDD_Lab;

import java.util.*;
import java.util.stream.Collectors;


public class Instock implements ProductStock {

    List<Product> instockProducts;

    public Instock() {
        this.instockProducts = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return instockProducts.size();
    }

    @Override
    public boolean contains(Product product) {
        return instockProducts.contains(product);
    }

    @Override
    public void add(Product product) {
        instockProducts.add(product);
    }

    @Override
    public void changeQuantity(String product, int quantity) {
        /*
        int productFound = 0;

        for (TDD_Lab.Product p : this.instockProducts) {
            if (p.getLabel().equals(product)) {
                p.setQuantity(quantity);
                productFound++;
                break;
            }
        }

        if(productFound == 0) {
            throw new IllegalArgumentException();
        }
        */


        Product matched = this.instockProducts
                .stream()
                .filter(p -> p.getLabel().equals(product))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        matched.setQuantity(quantity);
    }

    @Override
    public Product find(int index) {
        return instockProducts.get(index);
    }

    @Override
    public Product findByLabel(String label) {

        return this.instockProducts
                        .stream()
                        .filter(p -> p.getLabel().equals(label))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Iterable<Product> findFirstByAlphabeticalOrder(int count) {

        if(count <= 0 || count >= this.instockProducts.size()) {
            return new ArrayList<>();
        }

        return this.instockProducts
                .stream()
                .sorted(Comparator.comparing(Product::getLabel))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Product> findAllInRange(double lo, double hi) {

        return this.instockProducts
                .stream()
                .filter(p -> p.getPrice() > lo && p.getPrice() <= hi)
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .collect(Collectors.toList());

    }

    @Override
    public Iterable<Product> findAllByPrice(double price) {
        return this.instockProducts
                .stream()
                .filter(p -> p.getPrice() == price)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Product> findFirstMostExpensiveProducts(int count) {

        if(count > this.instockProducts.size() || count <= 0) {
            throw new IllegalArgumentException();
        }

        return this.instockProducts
                .stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Product> findAllByQuantity(int quantity) {
        return this.instockProducts
                .stream()
                .filter(p -> p.getQuantity() == quantity)
                .collect(Collectors.toList());
    }

    @Override
    public Iterator<Product> iterator() {

        return this.instockProducts.iterator();
    }
}
