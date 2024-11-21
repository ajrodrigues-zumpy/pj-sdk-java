package inter.pix.cob;

import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.pix.model.Cobranca;
import inter.pix.model.CobrancaDetalhada;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_PIX_COB_WRITE;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_PIX_COBRANCAS_IMEDIATAS;

@Slf4j
public class RevisarCobrancaImediata {
    public CobrancaDetalhada revisar(Config config, Cobranca cobranca) throws SdkException {
        log.info("RevisarCobrancaImediata {} {}", config.getClientId(), cobranca.getTxid());
        try {
            String url = URL_PIX_COBRANCAS_IMEDIATAS.replace("AMBIENTE", config.getAmbiente()) + "/" + cobranca.getTxid();
            String json = JsonUtils.writePretty(cobranca);
            json = HttpUtils.callPatch(config, url, ESCOPO_PIX_COB_WRITE, "Erro ao revisar cobrança imediata", json);
            return JsonUtils.read(json, CobrancaDetalhada.class);
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
