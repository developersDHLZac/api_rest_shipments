package dhl.supplychain.api.au2dit.api.controllers;

import dhl.supplychain.api.au2dit.api.DHL_Models.DHL_Users;
import dhl.supplychain.api.au2dit.api.DHL_Repository.UserRepository;
import dhl.supplychain.api.au2dit.api.factura.Factura;
import dhl.supplychain.api.au2dit.api.factura.FacturaService;
import dhl.supplychain.api.au2dit.api.models.Facture;
import dhl.supplychain.api.au2dit.api.models.Item;
import dhl.supplychain.api.au2dit.api.models.User;
import dhl.supplychain.api.au2dit.api.pedimento.Pedimento;
import dhl.supplychain.api.au2dit.api.pedimento.PedimentoService;
import dhl.supplychain.api.au2dit.api.productoentrada.ProductoEntrada;
import dhl.supplychain.api.au2dit.api.productoentrada.ProductoEntradaServicio;
import dhl.supplychain.api.au2dit.api.registro.Registro;
import dhl.supplychain.api.au2dit.api.registro.RegistroService;
import dhl.supplychain.api.au2dit.api.token.Token;
import dhl.supplychain.api.au2dit.api.token.TokenService;
import fr.w3blog.zpl.constant.ZebraFont;
import fr.w3blog.zpl.model.ZebraLabel;
import fr.w3blog.zpl.model.ZebraPrintException;
import fr.w3blog.zpl.model.ZebraUtils;
import fr.w3blog.zpl.model.element.ZebraNativeZpl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class APIController {

    @Autowired
    FacturaService facturaService;

    @Autowired
    ProductoEntradaServicio productoEntradaServicio;

    @Autowired
    PedimentoService pedimentoService;

    @Autowired
    TokenService tokenService;

    @Autowired
    RegistroService registroService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/rastreo/{codigo}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('AUDITOR')")
    public List<Facture> getItems(@PathVariable("codigo") String codigo) {
        List<Factura> facturas= facturaService.findByRastreo(codigo.trim());
        List<Facture> factures= new ArrayList<>();
        Set<Item> items;
        Facture facture;
        Item item;
        if (facturas != null && !facturas.isEmpty()){
            for ( Factura factura: facturas){
                if (factura.getStatus().equals("Pendiente") || factura.getStatus().equals("En proceso")){
                    items = new HashSet<>();
                    facture = new Facture();
                    facture.setEmpaqueList(factura.getEmpaqueList());
                    facture.setEmpaqueNo(factura.getEmpaqueNo());
                    facture.setNumFactura(factura.getNumFactura());
                    facture.setStatus(factura.getStatus());
                    facture.setNoPedimento(factura.getPedimento().getNoPedimento());
                    for (ProductoEntrada pe:productoEntradaServicio.findByFactura(factura)){
                        item = new Item();
                        item.setCodigo(pe.getCodigo());
                        item.setCantidad(pe.getCantidad());
                        item.setDescripcion(pe.getDescripcion());
                        item.setId(pe.getIdProductoEntrada());
                        item.setLocalidad(pe.getLocalidad());
                        item.setCantidadRegistrada(pe.getCantidadReportada());
                        item.setMaltratados(0);
                        item.setCambiados(0);
                        item.setSobrantes(0);
                        item.setPieza(true);
                        item.setConsecutivo(pe.getConsecutivo());
                        item.setOrden(pe.getOrden().getIdOrden());
                        items.add(item);
                    }
                    facture.setItems(items);
                    factures.add(facture);
                    factura.setStatus("En proceso");
                    if (factura.getStartTime()==null)
                        factura.setStartTime(new Date());
                    if (factura.getPedimento().getStatus().equals("Pendiente")){
                        factura.getPedimento().setStatus("En proceso");
                        pedimentoService.guardar(factura.getPedimento());
                    }
                    factura.setAuditor(SecurityContextHolder.getContext().getAuthentication().getName());
                    facturaService.guardar(factura);
                }

            }
        }

        return factures;
    }

    @RequestMapping(value = "/ingreso" , method = RequestMethod.POST)
    @PreAuthorize("hasAnyAuthority('AUDITOR')")
    public String saveItem(@RequestBody Item item) {
        String username=SecurityContextHolder.getContext().getAuthentication().getName();
        DHL_Users user = userRepository.findByUserName(username);
        ProductoEntrada pe = productoEntradaServicio.findByIdProductoEntrada(item.getId());
        //Verificar si no esta auditado ya
        if (pe.getCantidadReportada()>0){
            return "false";
        }
        
        //Asignar la cantidad reportada
        pe.setCantidadReportada(item.getCantidadRegistrada());
        //Asignar faltantes o sobrantes
        // if (pe.getCantidad()==pe.getCantidadReportada()){ // Etiqueta Normal (Cantidad Teorica == Cantidad Reportada)
        //     pe.setStatus("Terminado");
        // }
        // else if(pe.getCantidad()<pe.getCantidadReportada()){ // Etiqueta Sobrante (Cantidad Teorica < Cantidad Reportada)
        //     pe.setSobrante(pe.getCantidadReportada()-pe.getCantidad());
        //     pe.setStatus("Revision");
        //     agregarRegistro(pe,user,"El auditor : "+ user.getUserName() + " reportó  " + pe.getSobrante() + " piezas sobrantes");
        // }
        // else { // Etiqueta Faltante (Cantidad Teorica > Cantidad Reportada)
        //     pe.setFaltante(pe.getCantidad()-pe.getCantidadReportada());
        //     pe.setStatus("Revision");
        //     agregarRegistro(pe,user,"El auditor : "+ user.getUserName() + " reportó  " + pe.getFaltante() + " piezas faltantes");
        // }

        // //Asignar estado de las piezas
        // if (item.getMaltratados()>0){ // Etiqueta Dañada
        //     pe.setMalEstado(item.getMaltratados());
        //     pe.setStatus("Revision");
        //     agregarRegistro(pe,user,"El auditor : "+ user.getUserName() + " reportó  " + item.getMaltratados() + " piezas dañadas");
        // }
        // if (item.getCambiados()>0){ // Etiqueta Camabiada
        //     pe.setCambiado(item.getCambiados());
        //     pe.setStatus("Revision");
        //     agregarRegistro(pe,user,"El auditor : "+ user.getUserName() + " reportó  " + item.getCambiados() + " piezas cambiadas");
        // }

        printLabel(pe, item);
        
        productoEntradaServicio.save(pe);

        // System.out.println("====================================================================");

        return "true";
    }

    @RequestMapping(value = "/verify/{codigo}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('AUDITOR')")
    public boolean verifyFacture(@PathVariable("codigo") String codigo) {
        boolean correcto=true;
        boolean revision=false;
        Factura factura= facturaService.findByNum(codigo);
        List<ProductoEntrada> items = productoEntradaServicio.findByFactura(factura);
        for (ProductoEntrada pe: items) {
            if(pe.getStatus().equals("Pendiente")){
                return false;
            }

            if (pe.getStatus().equals("Revision")){
                revision=true;
                correcto=false;
            }
        }
        if (revision){
            factura.setStatus("Revision");
        }
        factura.setEndTime(new Date());
        if (!correcto){
            if(tokenService.getByFactura(factura)==null){
                Token token = new Token();
                token.setFactura(factura);
                token.setToken(randomAlphaNumeric(6));
                tokenService.guardar(token);
                facturaService.guardar(factura);
            }
            saveTotalTime(factura);
            return false;
        }
        factura.setStatus("Terminado");
        facturaService.guardar(factura);
        saveTotalTime(factura);
        closePedimento(factura);
        return correcto;
    }

    @RequestMapping(value = "/unlock/{factura}/{code}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('AUDITOR')")
    public boolean verifyFacture(@PathVariable("code") String codigo,@PathVariable("factura") String numFactura) {
        Factura factura = facturaService.findByNum(numFactura);
        Token token =tokenService.getByFactura(factura);
        if (token.getToken().equals(codigo)){
            for (ProductoEntrada pe:factura.getProductoEntradas()){
                if (pe.getStatus().equals("Revision")){
                    pe.setEditable(true);
                    productoEntradaServicio.save(pe);
                }

            }
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/foto/{numFactura}", method = RequestMethod.POST)
    @PreAuthorize("hasAnyAuthority('AUDITOR')")
    public boolean uploadFile(@PathVariable("numFactura") String codigo, @RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException{
        Factura factura = facturaService.findByNum(codigo);
        if (file!=null && !file.isEmpty() ){
            String path = "C:\\uploads\\" + factura.getPedimento().getNoPedimento()+"\\" +factura.getNumFactura()+"\\"+  file.getOriginalFilename();
            File imageFile = new File(path);
            if (!imageFile.exists()){
                imageFile.mkdirs();
                imageFile.createNewFile();
            }
            try {
                file.transferTo(imageFile);
                BufferedImage bufferedImage = ImageIO.read(new File(path));
                File imageSaved = new File(path);
                ImageIO.write(bufferedImage, "png", imageSaved);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void labelDesign(int type, ProductoEntrada pe, int qtyLabel)
    {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String actualDay = dateFormat.format(date);

        String rpta = "";

        switch (type) {
            case 1: // Etiqueta normal
                rpta = "^XA";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD1 Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    // System.out.println("Etiqueta " + qtyLabel + " Normal Impresa");
                break;
            case 2: // Etiqueta dañada
                rpta = "^XA";
                    rpta += "^FO120,0^FB370,4,,^A0B,26,28^FDD^FS";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD1 Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    // System.out.println("Etiqueta " + qtyLabel + " Dañada Impresa");
                break;
            case 3: // Etiqueta cambiada
                rpta = "^XA";
                    rpta += "^FO120,0^FB370,4,,^A0B,26,28^FDC^FS";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD1 Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    // System.out.println("Etiqueta " + qtyLabel + " Cambiada Impresa");     
                break;
            case 4: // Etiqueta sobrante
                rpta = "^XA";
                    rpta += "^FO120,0^FB370,4,,^A0B,26,28^FDS^FS";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD1 Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    // System.out.println("Etiqueta " + qtyLabel + " Sobrante Impresa");
                break;
            case 5: // Etiqueta sobrante dañada
                rpta = "^XA";
                    rpta += "^FO120,0^FB370,4,,^A0B,26,28^FDSD^FS";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD1 Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    // System.out.println("Etiqueta " + qtyLabel + " SD impresa");
                break;
            case 9: // Etiqueta sobrante cambiada
                rpta = "^XA";
                    rpta += "^FO120,0^FB370,4,,^A0B,26,28^FDSC^FS";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD1 Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    // System.out.println("Etiqueta " + qtyLabel + " SC impresa");
                break;
            case 6: // Imprimir para bultos (teórica)
                rpta = "^XA";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD"+ pe.getCantidad() +" Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    System.out.println("Etiqueta de Bulto (Teorica: "+ pe.getCantidad() +") impresa");
                break;
            case 7: // Imprimir para bultos (sobrante)
                rpta = "^XA";
                    rpta += "^FO120,0^FB370,4,,^A0B,26,28^FDS^FS";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD"+ (pe.getCantidadReportada() - pe.getCantidad()) +" Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    System.out.println("Etiqueta de Bulto (Sobrante: "+ (pe.getCantidadReportada() - pe.getCantidad()) +") impresa");
                break;
            case 8: // Imprimir para Bultos (cantidad sobrante < cantidad teórica)
                rpta = "^XA";
                    rpta += "^FO160,0^FB370,4,,^A0B,22,24^FDNo. de parte: "+ pe.getCodigo() +"^FS";
                    rpta += "^FO190,0^FB350,4,,^A0B,22,24^FDMarca: SIN MARCA^FS";
                    rpta += "^FO225,80^FB320,4,,^A0B,60,66^FDContenido:^FS";
                    rpta += "^FO295,60^FB320,4,,^A0B,60,66^FD"+ pe.getCantidadReportada() +" Pieza^FS";
                    rpta += "^FO350,100^FB320,4,4,C^A0B,16,16^FDDESCRIPCION:\\&"+ pe.getDescripcion() +"^FS";
                    rpta += "^FO400,30^FB300,4,,^ABB,1,5^FDPAIS ORIGEN: "+ pe.getPaisOrigen() +"^FS";
                    rpta += "^FO430,100^FB320,4,4,C^A0B,16,16^FDIMPORTADOR: SANDVIK MINING AND CONSTRUCTION DE MEXICO SA DE CV^FS";
                    rpta += "^FO480,100^FB320,4,4,C^A0B,16,16^FDDOMICILIO:Benjamin Franklin Lote 8 \\&Manzana 1 Tlajomulco de Zuniga Jalisco, \\&Mexico CP 45640^FS";
                    rpta += "^FO560,90^FB300,4,,^APB,1,5^FD"+ actualDay +"^FS";
                    rpta += "^FO560,50^FB260,4,,^APB,1,5^FD"+ pe.getFactura().getPedimento().getNoPedimento() +"^FS";
                    rpta += "^FO560,10^FB165,4,,^APB,1,5^FD" + pe.getFactura().getNumFactura() +"^FS";
                    rpta += "^FO605,120^GB80,300,100^FS";
                    rpta += "^LRY";
                    rpta += "^FO620,160,^AUB,1,5^FD"+ pe.getLocalidad()  +"^FS";
                    rpta += "^FO238,5^BY2^BC,100,N,N,N,A^FD"+ pe.getCodigoEscaneo() +"^FS";
                    rpta += "^XZ";
                    System.out.println("Etiqueta de Bulto (Reportada: "+ pe.getCantidadReportada() +") impresa");
                break;

            default:
                rpta = "Tipo de etiqueta no considerada.";
                break;
        }

        ZebraLabel zebraLabel = new ZebraLabel(790, 540);
        zebraLabel.setDefaultZebraFont(ZebraFont.ZEBRA_ZERO);

        zebraLabel.addElement(new ZebraNativeZpl(rpta));

        zebraLabel.getZplCode();

        try {
            ZebraUtils.printZpl(zebraLabel, "192.168.0.109", 9100);
            // ZebraUtils.printZpl(zebraLabel, "192.168.0.101", 9100);
        } catch (ZebraPrintException e) { }

    }

    private void printLabelFinal(ProductoEntrada pe, Item item)
    {
        // Asiganción de Variables
        // Cantidad Teorica
        int cantidadTeorica = pe.getCantidad();
        // Cantidad Reportada
        int cantidadReportada = item.getCantidadRegistrada();
        // Cantidad Dañada
        int cantidadMaltratada = item.getMaltratados();
        // Cantidad Cambiada
        int cantidadCambiada = item.getCambiados();
        // Cantidad Sobrante
        int cantidadSobrante = 0;
        // Cantidad Faltante
        int cantidadFaltante = 0;

        if (cantidadReportada > cantidadTeorica)
            cantidadSobrante = cantidadReportada - cantidadTeorica; // 5 R - 3 T - 3 D - 3 SD - 3 N - 2 S

        if (cantidadReportada == cantidadTeorica) // 10 R - 10 T - 5 D - 5 C | 10 N - 5 D | 10 N - 5 D - 5 C
            cantidadSobrante = 0;

        if (cantidadReportada < cantidadTeorica) // 9 R - 10 T - 2 D - 2 C | 9 N | 7 N - 2 D | 5 N - 2 D - 2 C
            cantidadFaltante = cantidadTeorica - cantidadReportada;

        // 5 - 2 - 0 - 5 - 0 | 5 N - 2SD
        if (cantidadReportada == cantidadTeorica && cantidadMaltratada != 0 && cantidadCambiada == 0 && (cantidadMaltratada + cantidadCambiada + cantidadSobrante) != 0)
        {
            // Imprimir Etiqueta SD
            for (int i = 1; i <= cantidadMaltratada; i++)
            {
                labelDesign(5, pe, i);
            }
            // Imprimir Etiqueta N
            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 15 - 0 - 2 - 10 - 0 | 10 N - 2 SC - 5 S
        else if (cantidadReportada > cantidadTeorica && cantidadMaltratada == 0 && cantidadCambiada != 0 && cantidadSobrante != 0)
        {
            for (int i = 1; i <= cantidadCambiada; i++) // Etiqueta SC
            {
                labelDesign(9, pe, i);
            }

            for (int i = 1; i <= cantidadSobrante; i++) // Etiqueta S
            {
                labelDesign(4, pe, i);
            }

            for (int i = 1; i <= cantidadTeorica; i++)
            {
                labelDesign(1, pe, i);
            }
        
        }
        // 15 - 2 - 0 - 10 - 0 | 10 N 2 SD - 5S
        else if (cantidadReportada > cantidadTeorica && cantidadMaltratada != 0 && cantidadCambiada == 0 && cantidadSobrante != 0)
        {
            for (int i = 1; i <= cantidadMaltratada; i++) // Etiqueta SC
            {
                labelDesign(5, pe, i);
            }

            for (int i = 1; i <= cantidadSobrante; i++) // Etiqueta S
            {
                labelDesign(4, pe, i);
            }

            for (int i = 1; i <= cantidadTeorica; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 15 - 2 - 2 - 10 - 0 | 10 N - 2 SD - 2 SC - 5 S
        else if (cantidadReportada > cantidadTeorica && cantidadMaltratada != 0 && cantidadCambiada != 0 && cantidadSobrante != 0)
        {
            
            for (int i = 1; i <= cantidadMaltratada; i++)
            {
                labelDesign(5, pe, i);
            }

            for (int i = 1; i <= cantidadCambiada; i++)
            {
                labelDesign(9, pe, i);
            }

            for (int i = 1; i <= cantidadSobrante; i++)
            {
                labelDesign(4, pe, i);
            }

            for (int i = 1; i <= cantidadTeorica; i++)
            {
                labelDesign(1, pe, i);
            }
        
        }
        // 15 - 0 - 0 - 10 - 0
        else if (cantidadReportada > cantidadTeorica && cantidadMaltratada == 0 && cantidadCambiada == 0 && cantidadSobrante != 0)
        {
            for (int i = 1; i <= cantidadSobrante; i++)
            {
                labelDesign(4, pe, i);
            }

            for (int i = 1; i <= cantidadTeorica; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 9 - 0 - 0 - 10 - 1
        else if (cantidadReportada < cantidadTeorica && cantidadMaltratada == 0 && cantidadCambiada == 0 && cantidadFaltante != 0)
        {
            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 10 - 0 - 0 - 10 - 0
        else if (cantidadReportada == cantidadTeorica && cantidadMaltratada == 0 && cantidadCambiada == 0 && cantidadFaltante == 0)
        {
            for (int i = 1; i <= cantidadTeorica; i++)
            {
                labelDesign(1, pe, i);
            } 
        }
        // 8 - 0 - 2 - 10 - 0
        else if (cantidadReportada < cantidadTeorica && cantidadMaltratada == 0 && cantidadCambiada != 0 && cantidadFaltante != 0)
        {
            if ((cantidadReportada + cantidadCambiada > cantidadTeorica))
            {
                for (int i = 1; i <= ((cantidadReportada + cantidadCambiada) - cantidadTeorica); i++)
                {
                    labelDesign(9, pe, i);
                }

                for (int i = 1; i <= cantidadCambiada - ((cantidadReportada + cantidadCambiada) - cantidadTeorica); i++)
                {
                    labelDesign(3, pe, i);
                }
            }
            else
            {
                for (int i = 1; i <= cantidadCambiada; i++)
                {
                    labelDesign(3, pe, i);
                }
            }

            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 8 - 1 - 1 - 10 - 0
        else if (cantidadReportada < cantidadTeorica && cantidadMaltratada != 0 && cantidadCambiada != 0)
        {
            int qtySDFinal = 0;
            int qtySCFinal = 0;
            int qtyD = 0;
            int qtyC = 0;

            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }

            if (cantidadMaltratada > cantidadCambiada)
            {
                if (cantidadMaltratada > cantidadReportada)
                {

                    for (int i = 1; i <= cantidadMaltratada; i++)
                    {
                        labelDesign(2, pe, i);
                    }

                    qtyC = cantidadTeorica - (cantidadReportada + cantidadMaltratada);
                    qtySCFinal = cantidadCambiada - qtyC;

                    for (int i = 1; i <= qtyC; i++)
                    {
                        labelDesign(3, pe, i);
                    }

                    for (int i = 1; i <= qtySCFinal; i++)
                    {
                        labelDesign(9, pe, i);
                    }
                }
                else
                {
                    for (int i = 1; i <= cantidadMaltratada; i++)
                    {
                        labelDesign(2, pe, i);
                    }

                    qtyC = cantidadTeorica - (cantidadReportada + cantidadMaltratada);
                    qtySCFinal = cantidadCambiada - qtyC;

                    for (int i = 1; i <= qtyC; i++)
                    {
                        labelDesign(3, pe, i);
                    }

                    for (int i = 1; i <= qtySCFinal; i++)
                    {
                        labelDesign(9, pe, i);
                    }
                }
            }
            else
            {
                // Calcular resultado de Cantidad Teoruica - Cantidad Reportada
                if ((cantidadTeorica - cantidadReportada) > cantidadMaltratada)
                {
                    qtyC = ((cantidadTeorica - cantidadReportada) - cantidadMaltratada);
                    qtySCFinal = cantidadCambiada - qtyC;

                    for (int i = 1; i <= cantidadMaltratada; i++)
                    {
                        labelDesign(2, pe, i);
                    }

                    for (int i = 1; i <= qtyC; i++)
                    {
                        labelDesign(3, pe, i);
                    }

                    for (int i = 1; i <= qtySCFinal; i++)
                    {
                        labelDesign(9, pe, i);
                    }
                }
                else
                {
                    qtyD = cantidadMaltratada - (cantidadTeorica - cantidadReportada);
                    qtySDFinal = cantidadMaltratada - qtyD;

                    for (int i = 1; i <= qtyD; i++)
                    {
                        labelDesign(2, pe, i);
                    }

                    for (int i = 1; i <= qtySDFinal; i++)
                    {
                        labelDesign(5, pe, i);
                    }

                    for (int i = 1; i <= cantidadCambiada; i++)
                    {
                        labelDesign(9, pe, i);
                    }
                }
            }
        }
        // 9 - 1 - 0 - 10 - 0
        else if (cantidadReportada < cantidadTeorica && cantidadMaltratada != 0 && cantidadCambiada == 0)
        {
            if ((cantidadReportada + cantidadMaltratada) > cantidadTeorica)
            {
                for (int i = 1; i <= ((cantidadReportada + cantidadMaltratada) - cantidadTeorica); i++)
                {
                    labelDesign(5, pe, i);
                }

                for (int i = 1; i <= cantidadMaltratada - ((cantidadReportada + cantidadMaltratada) - cantidadTeorica); i++)
                {
                    labelDesign(2, pe, i);
                }
            }
            else
            {
                for (int i = 1; i <= cantidadMaltratada; i++)
                {
                    labelDesign(2, pe, i);
                }
            }

            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 10 - 0 - 1 - 10 - 0
        else if (cantidadReportada == cantidadTeorica && cantidadMaltratada == 0 && cantidadCambiada != 0)
        {
            for (int i = 1; i <= cantidadCambiada; i++)
            {
                labelDesign(9, pe, i);
            }

            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 10 - 1 - 0 - 10 - 0
        else if (cantidadReportada == cantidadTeorica && cantidadMaltratada != 0 && cantidadCambiada == 0)
        {
            for (int i = 1; i <= cantidadMaltratada; i++)
            {
                labelDesign(5, pe, i);
            }

            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 10 - 1 - 1 - 10 - 0
        else if (cantidadReportada == cantidadTeorica && cantidadMaltratada != 0 && cantidadCambiada != 0)
        {
            for (int i = 1; i <= cantidadCambiada; i++)
            {
                labelDesign(9, pe, i);
            }

            for (int i = 1; i <= cantidadMaltratada; i++)
            {
                labelDesign(5, pe, i);
            }

            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        // 7 - 1 - 1 - 10 - 0
        else if (cantidadReportada < cantidadTeorica && cantidadMaltratada != 0 && cantidadCambiada != 0 && cantidadFaltante != 0)
        {
            if ((cantidadReportada + cantidadMaltratada + cantidadCambiada) > cantidadTeorica)
            {
                // Qty SD
                int qtySD = (cantidadReportada + cantidadMaltratada) - cantidadTeorica;

                // Cantidad SD
                for (int i = 1; i <= qtySD; i++)
                {
                    labelDesign(5, pe, i);
                }

                // Cantidad D
                for (int i = 1; i <= cantidadMaltratada - qtySD; i++)
                {
                    labelDesign(2, pe, i);
                }

                for (int i = 1; i <= cantidadCambiada; i++)
                {
                    labelDesign(9, pe, i);
                }
            }
            else
            {
                for (int i = 1; i <= cantidadMaltratada; i++)
                {
                    labelDesign(2, pe, i);
                }

                for (int i = 1; i <= cantidadCambiada; i++)
                {
                    labelDesign(3, pe, i);
                }
            }
            
            for (int i = 1; i <= cantidadReportada; i++)
            {
                labelDesign(1, pe, i);
            }
        }
        else if (cantidadReportada == 0 && cantidadMaltratada == cantidadTeorica && cantidadCambiada == 0)
        {
            for (int i = 1; i <= cantidadMaltratada; i++)
            {
                labelDesign(2, pe, i);
            }
        }
        else if (cantidadReportada == 0 && cantidadMaltratada == 0 && cantidadCambiada == cantidadTeorica)
        {
            for (int i = 1; i <= cantidadCambiada; i++)
            {
                labelDesign(3, pe, i);
            }
        }
        else if (cantidadReportada == 0  && cantidadMaltratada != 0 && cantidadCambiada != 0 && (cantidadMaltratada + cantidadCambiada) == cantidadTeorica)
        {
            for (int i = 1; i <= cantidadMaltratada; i++)
            {
                labelDesign(2, pe, i);
            }
            for (int i = 1; i <= cantidadCambiada; i++)
            {
                labelDesign(3, pe, i);
            }
        }
        else if (cantidadReportada == 0 && cantidadMaltratada != 0 && cantidadCambiada != 0 && (cantidadMaltratada + cantidadCambiada) > cantidadTeorica)
        {
            // Qty SAny
            int qtySAny = 0;

            if (cantidadMaltratada > cantidadCambiada)
            {
                qtySAny = cantidadMaltratada - cantidadCambiada;

                for (int i = 1; i <= qtySAny; i++)
                {
                    labelDesign(5, pe, i);
                }

                for (int i = 1; i <= cantidadMaltratada - qtySAny; i++)
                {
                    labelDesign(2, pe, i);
                }

                for (int i = 1; i <= cantidadCambiada; i++)
                {
                    labelDesign(3, pe, i);
                }
            }
            else
            {
                for (int i = 1; i <= cantidadMaltratada; i++)
                {
                    labelDesign(2, pe, i);
                }

                qtySAny = cantidadCambiada - cantidadMaltratada;

                for (int i = 1; i <= cantidadCambiada - qtySAny; i++)
                {
                    labelDesign(3, pe, i);
                }

                for (int i = 1; i <= qtySAny; i++)
                {
                    labelDesign(9, pe, i);
                }
            }
        }
        else if (cantidadReportada == 0 && (cantidadMaltratada + cantidadCambiada) != 0 && cantidadFaltante != 0 && (cantidadMaltratada + cantidadCambiada) < cantidadTeorica)
        {
            if (cantidadMaltratada != 0 && cantidadCambiada == 0)
            {
                for (int i = 1; i <= cantidadMaltratada; i++)
                {
                    labelDesign(2, pe, i);
                }
            }
            else if (cantidadCambiada != 0 && cantidadMaltratada == 0)
            {
                for (int i = 1; i <= cantidadCambiada; i++)
                {
                    labelDesign(3, pe, i);
                }
            }
            else if (cantidadCambiada != 0 && cantidadMaltratada != 0)
            {
                for (int i = 1; i <= cantidadCambiada; i++)
                {
                    labelDesign(3, pe, i);
                }
                for (int i = 1; i <= cantidadMaltratada; i++)
                {
                    labelDesign(3, pe, i);
                }
            }
        }
    }

    private void printLabel(ProductoEntrada productoEntrada, Item item)
    {
        // Assign Values to Variable
        int reportedQuantity = item.getCantidadRegistrada(); // Cantidad Reportada
        int damagedQuantity = item.getMaltratados(); // Cantidad Dañada
        int amountChanged = item.getCambiados(); // Cantidad Cambiada
        int theoricalAmount = productoEntrada.getCantidad(); // Cantidad Teorica

        int amountRemaining = 0; // Cantidad Sobrante
        int missingAmount = 0; // Cantidad Faltante

        if (reportedQuantity > theoricalAmount)
            amountRemaining = reportedQuantity - theoricalAmount;
        
        if (reportedQuantity < theoricalAmount)
            missingAmount = theoricalAmount - reportedQuantity;

        if (item.getPieza() == true)
        {
            /**
             * R - D - S - C - T - F - N
             */
            // Conditionals (Verify with Lucio it's All Conditions or not)
            // 1 - 0 - 0 - 0 - 1 - 0 - 1
            if (reportedQuantity == theoricalAmount && damagedQuantity == 0 && amountChanged == 0 && amountRemaining == 0 && missingAmount == 0)
            {
                // labelDesign(1, productoEntrada);

                for (int i = 0; i < theoricalAmount; i++)
                {
                    labelDesign(1, productoEntrada, i);
                }
            }
            // 1 - 0 - 0 - 0 - 2 - 1 - 1
            else if (reportedQuantity < theoricalAmount && damagedQuantity == 0 && amountChanged == 0 && missingAmount != 0)
            {
                for (int i = 0; i < reportedQuantity; i++)
                {
                    labelDesign(1, productoEntrada, i);
                }
            }
            // 1 - 1 - 0 - 0 - 1 - 0 - 0
            else if (reportedQuantity == theoricalAmount && damagedQuantity != 0 && amountChanged == 0 && amountRemaining == 0 && missingAmount == 0)
            {
                // 10 T - 10 R - 5 D
                // 5 N - 5 D

                if (damagedQuantity == theoricalAmount)
                {
                    for (int i = 0; i < theoricalAmount; i++)
                    {
                        labelDesign(2, productoEntrada, i);
                    }
                }
                else
                {
                    for (int i = 0; i < damagedQuantity; i++)
                    {
                        labelDesign(2, productoEntrada, i);
                    }

                    for (int i = 0; i < (reportedQuantity - damagedQuantity); i++)
                    {
                        labelDesign(1, productoEntrada, i);
                    }
                }
            }
            // 2 - 0 - 1 - 0 - 1 - 0 - 1
            else if (reportedQuantity > theoricalAmount && damagedQuantity == 0 && amountChanged == 0 && amountRemaining != 0)
            {
                for (int i = 0; i < theoricalAmount; i++)
                {
                    labelDesign(1, productoEntrada, i);
                }

                for (int i = 0; i < amountRemaining; i++)
                {
                    labelDesign(4, productoEntrada, i);
                }
            }
            // 3 - 0 - 0 - 2 - 3 - 0 - 1
            else if (reportedQuantity == theoricalAmount && damagedQuantity == 0 && amountChanged != 0 && amountRemaining == 0 && missingAmount == 0)
            {
                for (int i = 0; i < (reportedQuantity - amountChanged); i++)
                {
                    labelDesign(1, productoEntrada, i);
                }

                for (int i = 0; i < amountChanged; i++)
                {
                    labelDesign(3, productoEntrada, i);
                }
            }
            // 5 - 2 - 0 - 2 - 5 - 0 - 1
            else if (reportedQuantity == theoricalAmount && damagedQuantity != 0 && amountChanged != 0 && amountRemaining == 0 && missingAmount == 0)
            {
                if ((damagedQuantity + amountChanged) < theoricalAmount)
                {
                    for (int i = 0; i < (theoricalAmount - (damagedQuantity + amountChanged)); i++)
                    {
                        labelDesign(1, productoEntrada, i);
                    }
                }

                for (int i = 0; i < damagedQuantity; i++)
                {
                    labelDesign(2, productoEntrada, i);
                }

                for (int i = 0; i < amountChanged; i++)
                {
                    labelDesign(3, productoEntrada, i);
                }
            }
            // 8 - 3 - 0 - 2 - 10 - 2 - 3
            else if (reportedQuantity < theoricalAmount && damagedQuantity != 0 && amountChanged != 0 && missingAmount != 0)
            {
                if ((damagedQuantity + amountChanged) < reportedQuantity)
                {
                    for (int i = 0; i < (reportedQuantity - (damagedQuantity + amountChanged)); i++)
                    {
                        labelDesign(1, productoEntrada, i);
                    }
                }

                for (int i = 0; i < damagedQuantity; i++)
                {
                    labelDesign(2, productoEntrada, i);
                }

                for (int i = 0; i < amountChanged; i++)
                {
                    labelDesign(3, productoEntrada, i);
                }
            }
            // 13 - 2 - 3 - 2 - 10 - 0 - 9
            else if (reportedQuantity > theoricalAmount && damagedQuantity != 0 && amountChanged != 0 && amountRemaining != 0)
            {
                // Etiqueta SD
                if (damagedQuantity > amountRemaining) // 4 > 11
                {

                    if (amountChanged == amountRemaining) // 3 == 2
                    {
                        for (int i = 0; i < damagedQuantity; i++)
                        {
                            labelDesign(2, productoEntrada, i);
                        }    
                    }
                    else if (amountChanged < amountRemaining) // 3 < 2
                    {
                        int qtyDamage = (theoricalAmount - (reportedQuantity - (damagedQuantity + amountChanged)));

                        // Dañada
                        for (int i = 0; i < qtyDamage; i++)
                        {
                            labelDesign(2, productoEntrada, i);
                        }
                        
                        for (int i = 0; i < damagedQuantity - qtyDamage; i++) 
                        {
                            labelDesign(5, productoEntrada, i);
                        }
                    }
                }
                else // 4 < 11
                {
                    if ((damagedQuantity + amountChanged) > amountRemaining)
                    {
                        // Etiquetas Sobrantes
                        int qDamage = amountRemaining - damagedQuantity;

                        for (int i = 0; i < qDamage; i++)
                        {
                            labelDesign(2, productoEntrada, i);
                        }
                        for (int i = 0; i < damagedQuantity - qDamage; i++)
                        {
                            labelDesign(5, productoEntrada, i);
                        }
                    }
                    else
                    {
                        // for (int i = 0; i < (damagedQuantity + amountChanged) - amountRemaining; i++)
                        // {
                        //     labelDesign(4, productoEntrada, i);
                        // }

                        for (int i = 0; i < damagedQuantity; i++)
                        {
                            labelDesign(5, productoEntrada, i);
                        }
                    }
                }

                if ((damagedQuantity + amountChanged) < amountRemaining)
                {
                    // Etqiuetas Sobrantes
                    for (int i = 0; i < (amountRemaining - (damagedQuantity + amountChanged)); i++)
                    {
                        labelDesign(4, productoEntrada, i);
                    }
                    amountRemaining = amountRemaining - (damagedQuantity + amountChanged);

                    // Etiquetas Cambiadas
                    for (int i = 0; i < amountChanged; i++)
                    {
                        labelDesign(3, productoEntrada, i);
                    }

                    for (int i = 0; i < (reportedQuantity - (damagedQuantity + amountChanged + amountRemaining)); i++)
                    {
                        labelDesign(1, productoEntrada, i);
                    }
                }
                else 
                {
                    // Etiquetas Cambiadas
                    for (int i = 0; i < amountChanged; i++)
                    {
                        labelDesign(3, productoEntrada, i);
                    }

                    for (int i = 0; i < (reportedQuantity - (damagedQuantity + amountChanged)); i++)
                    {
                        labelDesign(1, productoEntrada, i);
                    }
                }

            }
            // 13 - 0 - 3 - 0 - 10 - 0 - 10
            else if (reportedQuantity > theoricalAmount && damagedQuantity == 0 && amountChanged == 0 && amountRemaining != 0)
            {
                for (int i = 0; i < theoricalAmount; i++)
                {
                    labelDesign(1, productoEntrada, i);
                }

                for(int i = 0; i < amountRemaining; i++)
                {
                    labelDesign(4, productoEntrada, i);
                }
            }
            // 15 R - 6 D - 0 C - 5 S - 10 T
            else if (reportedQuantity > theoricalAmount && damagedQuantity != 0 && amountChanged == 0 && amountRemaining != 0)
            {
                if (damagedQuantity > amountRemaining)
                {
                    for (int i = 0; i < amountRemaining; i++)
                    {
                        labelDesign(5, productoEntrada, i);
                    }

                    for (int i = 0; i < (damagedQuantity - amountRemaining); i++)
                    {
                        labelDesign(2, productoEntrada, i);
                    }

                    for (int i = 0; i < (reportedQuantity - damagedQuantity); i++)
                    {
                        labelDesign(1, productoEntrada, i);
                    }
                }
                else
                {
                    for (int i = 0; i < damagedQuantity; i++)
                    {
                        labelDesign(5, productoEntrada, i);
                    }

                    for (int i = 0; i < (amountRemaining - damagedQuantity); i++)
                    {
                        labelDesign(4, productoEntrada, i);
                    }

                    for (int i = 0; i < (reportedQuantity - amountRemaining); i++)
                    {
                        labelDesign(1, productoEntrada, i);
                    }
                }
            }
            else if (reportedQuantity > theoricalAmount && damagedQuantity == 0 && amountChanged != 0 && amountRemaining != 0)
            {
                for (int i = 0; i < amountChanged; i++)
                {
                    labelDesign(3, productoEntrada, i);
                }

                for (int i = 0; i < amountRemaining; i++)
                {
                    labelDesign(4, productoEntrada, i);
                }

                for (int i = 0; i < (reportedQuantity - (amountChanged + amountRemaining)); i++)
                {
                    labelDesign(1, productoEntrada, i);
                }
            }
            // 9 - 1 - 0 - 0 - 10 - 1 - 8
            else if (reportedQuantity < theoricalAmount && damagedQuantity != 0 && amountChanged == 0 && missingAmount != 0)
            {
                for (int i = 0; i < damagedQuantity; i++)
                {
                    labelDesign(2, productoEntrada, i);
                }

                for (int i = 0; i < (reportedQuantity - damagedQuantity); i++)
                {
                    labelDesign(1, productoEntrada, i);
                }
            }
            else if (reportedQuantity < theoricalAmount && damagedQuantity == 0 && amountChanged != 0 && missingAmount != 0)
            {
                for (int i = 0; i < amountChanged; i++)
                {
                    labelDesign(3, productoEntrada, i);
                }

                for (int i = 0; i < (reportedQuantity - amountChanged); i++)
                {
                    labelDesign(1, productoEntrada, i);
                }
            }
        }
        else
        {
            // No disponemos de identificador de etiqueta (SD, S, D, C)
            // Solo imprimiremos Normales
            if (reportedQuantity > theoricalAmount) // Imprimir 2 Etiquetas
            {
                /**
                 * PARAMS
                 * int type (Value of Conditional IF for evaluating type of label)
                 * ProductoEntrada productoEntrada (Object type ProductoEntrada)
                 * int qtyLabel (Value of number of label)
                 */
                labelDesign(6, productoEntrada, 0);
                labelDesign(7, productoEntrada, 0);
            }
            else // Imprimir 1 Etiqueta
            {
                labelDesign(8, productoEntrada, 0);
            }

            // if (reportedQuantity == theoricalAmount && damagedQuantity == 0 && amountChanged == 0)
            // {
            //     labelDesign(8, productoEntrada, 0);
            // }
            // else if (reportedQuantity > theoricalAmount && damagedQuantity == 0 && amountChanged == 0 && amountRemaining != 0)
            // {
            //     labelDesign(6, productoEntrada, 0);
            //     labelDesign(7, productoEntrada, 0);
            // }
            // else if (reportedQuantity < theoricalAmount && damagedQuantity == 0 && amountChanged == 0 && missingAmount!= 0)
            // {
            //     labelDesign(8, productoEntrada, 0);
            // }
        }
    }

    private void saveTotalTime(Factura factura) {
        Factura facturaTime= facturaService.findByNum(factura.getNumFactura());
        factura.setTotalTime(getDateDiff(facturaTime.getStartTime(),facturaTime.getEndTime()));
        facturaService.guardar(factura);
    }

    private void agregarRegistro(ProductoEntrada pe, User user,String mensaje){
        Registro registro = new Registro();
        registro.setProductoEntrada(pe);
        registro.setUser(user);
        registro.setFechaEntrada(new Date());
        registro.setDescripcion(mensaje);
        registroService.addRegistro(registro);
    }

    private void closePedimento(Factura factura){
        Pedimento pedimento = factura.getPedimento();
        List<Factura> facturas = facturaService.findByPedimento(pedimento);
        pedimento.setStatus("Terminado");
        for(Factura fac:facturas){
            if (fac.getStatus().equals("En proceso")){
                pedimento.setStatus("En proceso");
                break;
            }
            if (fac.getStatus().equals("Pendiente")){
                pedimento.setStatus("En proceso");
                break;
            }
            if (fac.getStatus().equals("Revision")){
                pedimento.setStatus("En proceso");
                break;
            }
            if (fac.getStatus().equals("Completado")){
                pedimento.setStatus("Completado");
            }
        }
        pedimentoService.guardar(pedimento);
    }

    private long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillies);
    }

    private String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);
        return sb.toString();
    }
}
