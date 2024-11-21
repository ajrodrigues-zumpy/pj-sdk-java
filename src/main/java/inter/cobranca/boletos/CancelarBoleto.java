package inter.cobranca.boletos;

import inter.cobranca.model.RequisicaoCancelarBoleto;
import inter.cobranca.model.enums.MotivoCancelamento;
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
public class CancelarBoleto {

    public void cancelar(Config config, String nossoNumero, MotivoCancelamento motivoCancelamento) throws SdkException {
        log.info("CancelarBoleto {} {} {}", config.getClientId(), nossoNumero, motivoCancelamento);
        String url = URL_BOLETOS.replace("AMBIENTE", config.getAmbiente()) + "/" + nossoNumero + "/cancelar";
        RequisicaoCancelarBoleto request = RequisicaoCancelarBoleto.builder().motivoCancelamento(motivoCancelamento).build();
        try {
            String json = JsonUtils.writePretty(request);
            HttpUtils.callPost(config, url, ESCOPO_BOLETO_COBRANCA_WRITE, "Erro ao cancelar boleto", json);
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