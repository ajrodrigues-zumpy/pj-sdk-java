package inter.cobrancav3.cobranca;

import inter.cobrancav3.model.CobrancaRecuperada;
import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_BOLETO_COBRANCA_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_COBRANCAS;

@Slf4j
public class RecuperaCobranca {

    public CobrancaRecuperada recuperar(Config config, String codigoSolicitacao) throws SdkException {
        log.info("RecuperarCobranca {} codigoSolicitacao={}", config.getClientId(), codigoSolicitacao);
        String url = URL_COBRANCAS.replace("AMBIENTE", config.getAmbiente()) + "/" + codigoSolicitacao;
        String json = HttpUtils.callGet(config, url, ESCOPO_BOLETO_COBRANCA_READ, "Erro ao recuperar cobrança");
        try {
            return JsonUtils.read(json, CobrancaRecuperada.class);
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