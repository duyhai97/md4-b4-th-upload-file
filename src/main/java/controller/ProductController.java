package controller;



import model.Product;
import model.ProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import service.IProductService;
import service.ProductService;


import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {
 IProductService productService = new ProductService();

    @Autowired
    private Environment environment;

    @GetMapping()
    public ModelAndView listProduct(){
        ModelAndView modelAndView = new ModelAndView("/index");
        List<Product> productList = this.productService.findAll();
        modelAndView.addObject("productList", productList);
        return modelAndView;
    }

    @GetMapping("/create")
    public String showFormCreate(Model model){
        model.addAttribute("productForm", new ProductForm());
        return "/create";
    }

    //xu ly khi nhan nut tao moi
    @PostMapping("/create")
    public String createNewProduct(ProductForm productForm){
        //sao chep anh vao thu muc
        MultipartFile multipartFile = productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        String localFile = environment.getProperty("file_upload");
        try {
            FileCopyUtils.copy(multipartFile.getBytes(), new File(localFile+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Product product = new Product();
        product.setId((int) (Math.random() * 10000));
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());

        product.setImage(fileName);
        productService.save(product);
        return "redirect:/product";
    }

    @GetMapping("/{id}/update")
    public String showFormUpdate(@PathVariable int id, Model model){
        Product product = this.productService.findById(id);
        model.addAttribute("product", product);
        System.out.println(product);
        return "/update";
    }

    @PostMapping("/update")
    public String update(int id, Product product){
        this.productService.update(id,product);
        return "redirect:/product";
    }
}