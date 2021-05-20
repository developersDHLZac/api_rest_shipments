package dhl.supplychain.api.au2dit.api.token;


import dhl.supplychain.api.au2dit.api.factura.Factura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public void guardar(Token token){
        tokenRepository.save(token);
    }

    public Token getByFactura(Factura factura){
        return tokenRepository.findByFactura(factura);
    }
}
