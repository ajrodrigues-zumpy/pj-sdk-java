package inter.cobranca.boletos;

import inter.cobranca.model.Boleto;
import inter.cobranca.model.RespostaEmitirBoleto;
import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_BOLETO_COBRANCA_WRITE;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_BOLETOS;

@Slf4j
public class EmitirBoleto {

    public RespostaEmitirBoleto emitir(Config config, Boleto boleto) throws SdkException {
        log.info("EmitirBoleto {} {}", config.getClientId(), boleto.getSeuNumero());
        String url = URL_BOLETOS.replace("AMBIENTE", config.getAmbiente());
        try {
            String json = JsonUtils.writePretty(boleto);
            json = HttpUtils.callPost(config, url, ESCOPO_BOLETO_COBRANCA_WRITE, "Erro ao emitir boleto", json);
            return JsonUtils.read(json, RespostaEmitirBoleto.class);
        } catch (IOException ioException) {
            log.error(GENERIC_EXCEPTION_MESSAGE, ioException);
            throw new SdkException(
                    ioException.getMessage(),
                    Erro.builder()
                            .title(CERTIFICATE_EXCEPTION_MESSAGE).
                            detail(ioException.getMessage())
                            .build()
            );
        }
    }

}