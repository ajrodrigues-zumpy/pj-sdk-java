package inter.cobrancav3.cobranca;

import inter.cobrancav3.model.FiltroRecuperarSumarioCobrancas;
import inter.cobrancav3.model.Sumario;
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
import static inter.constants.Constants.URL_COBRANCAS_SUMARIO;

@Slf4j
public class RecuperaSumarioCobrancas {

    public Sumario recuperar(Config config, String dataInicial, String dataFinal, FiltroRecuperarSumarioCobrancas filtro) throws SdkException {
        log.info("RecuperarSumarioCobrancas {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        String url = URL_COBRANCAS_SUMARIO.replace("AMBIENTE", config.getAmbiente()) + "?dataInicial=" + dataInicial + "&dataFinal=" + dataFinal
                + addfilters(filtro);
        String json = HttpUtils.callGet(config, url, ESCOPO_BOLETO_COBRANCA_READ, "Erro ao recuperar sumário de cobrancas");
        try {
            return JsonUtils.read(json, Sumario.class);
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

    private String addfilters(FiltroRecuperarSumarioCobrancas filtro) {
        if (filtro == null) {
            return "";
        }

        StringBuilder filter = new StringBuilder();

        if (filtro.getFiltrarDataPor() != null) {
            filter.append("&filtrarDataPor").append("=").append(filtro.getFiltrarDataPor().toString());
        }

        if (filtro.getSituacao() != null) {
            filter.append("&situacao").append("=").append(filtro.getSituacao().toString());
        }

        if (filtro.getPessoaPagadora() != null) {
            filter.append("&pessoaPagadora").append("=").append(filtro.getPessoaPagadora());
        }

        if (filtro.getCpfCnpjPessoaPagadora() != null) {
            filter.append("&cpfCnpjPessoaPagadora").append("=").append(filtro.getCpfCnpjPessoaPagadora());
        }

        if (filtro.getSeuNumero() != null) {
            filter.append("&seuNumero").append("=").append(filtro.getSeuNumero());
        }

        if (filtro.getTipoCobranca() != null) {
            filter.append("&tipoCobranca").append("=").append(filtro.getTipoCobranca());
        }

        return filter.toString();
    }

}