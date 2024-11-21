package inter.cobrancav3.cobranca;

import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.model.RetornoPdf;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_BOLETO_COBRANCA_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_COBRANCAS;

@Slf4j
public class RecuperaCobrancaPdf {

    public void recuperar(Config config, String codigoSolicitacao, String arquivo) throws SdkException {
        log.info("RecuperarCobrancaPdf {} codigoSolicitacao={}", config.getClientId(), codigoSolicitacao);
        String url = URL_COBRANCAS.replace("AMBIENTE", config.getAmbiente()) + "/" + codigoSolicitacao + "/pdf";
        String json = HttpUtils.callGet(config, url, ESCOPO_BOLETO_COBRANCA_READ, "Erro ao recuperar cobrança pdf");
        try {
            RetornoPdf retornoPdf = JsonUtils.read(json, RetornoPdf.class);
            byte[] decodedBytes = Base64.getDecoder().decode(retornoPdf.getPdf());
            try (FileOutputStream stream = new FileOutputStream(arquivo)) {
                stream.write(decodedBytes);
            }
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