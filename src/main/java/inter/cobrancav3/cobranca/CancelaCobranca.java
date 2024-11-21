package inter.cobrancav3.cobranca;

import inter.cobrancav3.model.RequisicaoCancelarCobranca;
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
import static inter.constants.Constants.URL_COBRANCAS;

@Slf4j
public class CancelaCobranca {

    public void cancelar(Config config, String codigoSolicitacao, String motivoCancelamento) throws SdkException {
        log.info("CancelarCobranca {} {} {}", config.getClientId(), codigoSolicitacao, motivoCancelamento);
        String url = URL_COBRANCAS.replace("AMBIENTE", config.getAmbiente()) + "/" + codigoSolicitacao + "/cancelar";
        RequisicaoCancelarCobranca request = RequisicaoCancelarCobranca.builder().motivoCancelamento(motivoCancelamento).build();
        try {
            String json = JsonUtils.writePretty(request);
            HttpUtils.callPost(config, url, ESCOPO_BOLETO_COBRANCA_WRITE, "Erro ao cancelar cobranca", json);
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