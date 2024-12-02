package com.project.ims.service.impl;

import com.project.ims.model.dto.ProductDTOForShow;
import com.project.ims.model.dto.SupplierDTOForShow;
import com.project.ims.model.entity.Product;
import com.project.ims.model.entity.Supplier;
import com.project.ims.repository.ProductRepository;
import com.project.ims.repository.SupplierRepository;
import com.project.ims.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    @Transactional
    public Product addProduct(ProductDTOForShow productDTO) {
        Optional<Integer> existingProductIdOpt = productRepository.findProductIdByProductId(productDTO.getProductID());
        
        if (existingProductIdOpt.isPresent()) {
            Product existingProduct = productRepository.findById(existingProductIdOpt.get())
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            // them so luong vao sp da co
            existingProduct.setQuantity(existingProduct.getQuantity() + productDTO.getQuantity());
            existingProduct.setLastUpdate(new Date());
            

            return productRepository.save(existingProduct);
        }
        //tao sp moi tu productDTO
        
        Product newProduct = productDTO.constructFromDTO();
        newProduct.setLastUpdate(new Date());
        

        
        return productRepository.save(newProduct);
    }

 /*   public void addSupplierToProduct(int productId, String supplierName) { 
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Supplier supplier = supplierRepository.findByName(supplierName)
            .orElseGet(() -> { 
                Supplier newSupplier = new Supplier(); 
                newSupplier.setName(supplierName); 
                return supplierRepository.save(newSupplier); 
            }); 

        // Check if the relationship already exists in the join table
        if (!productSupplierRepository.existsByProductIdAndSupplierId(product.getProductID(), supplier.getsupplierID())) {
            ProductSupplier productSupplier = new ProductSupplier(product.getId(), supplier.getId());
            productSupplierRepository.save(productSupplier);  // Save to update the join table
        }
    }
    */
    @Override
    public Product findByProductId(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found."));
    }

    @Override
    public List<Product> filterProductsByName(String name) {
        return productRepository.findByProductNameContaining(name);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAllByOrderByLastUpdateDesc();
    }
    @Override
    public List<ProductDTOForShow> findAllDTO() {
        return productRepository.findAll().stream()
                .map(ProductDTOForShow::fromEntity)
                .collect(Collectors.toList());
    }


    
    @Override
    public Product updateProduct(int id, ProductDTOForShow productDTO) {
        Product existingProduct = findByProductId(id);
        if (productDTO.getProductName() != null) {
            existingProduct.setProductName(productDTO.getProductName());
        }
        if (productDTO.getCategory() != null) {
            existingProduct.setCategory(productDTO.getCategory());
        }
        if (productDTO.getPrice() != null) {
            existingProduct.setPrice(productDTO.getPrice());
        }
        if (productDTO.getUnitCal() != null) {
            existingProduct.setUnitCal(productDTO.getUnitCal());
        }
        existingProduct.setLastUpdate(new Date());
        return productRepository.save(existingProduct);
    }

   /*
    chay duoc
    */
    @Override
    public ProductDTOForShow deleteProduct(int id) {
        // Tìm sản phẩm theo ID
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Kiểm tra liên kết với import/export nếu cần
        if (product.getProductImports() != null && !product.getProductImports().isEmpty()) {
            throw new RuntimeException("Cannot delete product because of association with import records");
        }

        if (product.getProductExports() != null && !product.getProductExports().isEmpty()) {
            throw new RuntimeException("Cannot delete product because of association with export records");
        }

        // Xóa sản phẩm khỏi các nhà cung cấp
        if (product.getSuppliers() != null) {
            product.getSuppliers().forEach(supplier -> supplier.getProducts().remove(product));
        }

        // Chuyển đổi Product sang ProductDTO trước khi xóa
        ProductDTOForShow deletedProductDTO = ProductDTOForShow.fromEntity(product);

        // Xóa sản phẩm
        productRepository.deleteById(id);

        // Trả về thông tin của sản phẩm đã xóa
        return deletedProductDTO;
    }
    @Override
    public List<ProductDTOForShow> searchProducts(String query) {
        return productRepository.searchProducts(query).stream()
                .map(ProductDTOForShow::fromEntity)
                .collect(Collectors.toList());
    }

    
}