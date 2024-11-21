package inter.pix.cobv;

import inter.pix.model.CobrancaVencimentoDetalhada;
import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_PIX_COBV_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_PIX_COBRANCA_COM_VENCIMENTO;

@Slf4j
public class ConsultarCobrancaComVencimento {

    public CobrancaVencimentoDetalhada consultar(Config config, String txId) throws SdkException {
        log.info("ConsultarCobrancaComVencimento {} txId={}", config.getClientId(), txId);
        String url = URL_PIX_COBRANCA_COM_VENCIMENTO.replace("AMBIENTE", config.getAmbiente()) + "/" + txId;
        String json = HttpUtils.callGet(config, url, ESCOPO_PIX_COBV_READ, "Erro ao consultar cobrança com vencimento");
        try {
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
