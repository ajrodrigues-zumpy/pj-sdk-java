package inter.pix.cobv;

import inter.pix.model.CobrancaVencimento;
import inter.pix.model.CobrancaVencimentoDetalhada;
import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_PIX_COBV_WRITE;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_PIX_COBRANCA_COM_VENCIMENTO;

@Slf4j
public class RevisarCobrancaComVencimento {

    public CobrancaVencimentoDetalhada revisar(Config config, String txid, CobrancaVencimento cobranca) throws SdkException {
        log.info("RevisarCobrancaImediata {} {}", config.getClientId(), txid);
        try {
            String url = URL_PIX_COBRANCA_COM_VENCIMENTO.replace("AMBIENTE", config.getAmbiente()) + "/" + txid;
            String json = JsonUtils.writePretty(cobranca);
            json = HttpUtils.callPatch(config, url, ESCOPO_PIX_COBV_WRITE, "Erro ao revisar cobrança com vencimento", json);
            return JsonUtils.read(json, CobrancaVencimentoDetalhada.class);
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
