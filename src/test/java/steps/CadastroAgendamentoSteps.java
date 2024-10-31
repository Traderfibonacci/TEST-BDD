package steps;

import com.google.gson.JsonObject;
import com.networknt.schema.ValidationMessage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.junit.Assert;
import services.CadastroAgendamentoService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class CadastroAgendamentoSteps {
    CadastroAgendamentoService cadastroAgendamentoService = new CadastroAgendamentoService();
    JsonObject cadastroAtual;
    Response agendamentosListados;
    String baseUrl = "http://localhost:8080";

    @Dado("que eu tenha os seguintes dados da agendamento:")
    public void queEuTenhaOsSeguintesDadosDaAgendamento(List<Map<String, String>> rows) {
        for (Map<String, String> columns : rows) {
            cadastroAgendamentoService.setFieldsDelivery(columns.get("campo"), columns.get("valor"));
        }
    }

    @Dado("que eu tenha os seguintes dados da entrega:")
    public void queEuTenhaOsSeguintesDadosDaEntrega(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> columns : rows) {
            cadastroAgendamentoService.setFieldsDelivery(columns.get("campo"), columns.get("valor"));
        }
    }

    @Quando("eu enviar a requisição para o endpoint {string} de cadastro de agendamento")
    public void euEnviarARequisiçãoParaOEndpointDeCadastroDeAgendamento(String endPoint) {
        cadastroAgendamentoService.createDelivery(endPoint);
    }

    @Então("o status code da resposta deve ser {int}")
    public void oStatusCodeDaRespostaDeveSer(int statusCode) {
        assertEquals(statusCode, cadastroAgendamentoService.response.statusCode());
    }

    @Então("a resposta deve conter a mensagem de erro {string}")
    public void a_resposta_deve_conter_a_mensagem_de_erro(String mensagemEsperada) {
        String mensagemRecebida = cadastroAgendamentoService.obterMensagemDeErroDaResposta();
        assertEquals(mensagemEsperada, mensagemRecebida);
    }

    @Então("a resposta deve conter uma das seguintes mensagens de erro:")
    public void a_resposta_deve_conter_uma_das_seguintes_mensagens_de_erro(DataTable dataTable) {
        List<String> mensagensEsperadas = dataTable.asList(String.class);
        String responseBody = cadastroAgendamentoService.response.asString();
        JsonObject jsonResponse = cadastroAgendamentoService.gson.fromJson(responseBody, JsonObject.class);
        String mensagemRecebida = null;

        if (jsonResponse.has("nomeCliente")) {
            mensagemRecebida = jsonResponse.get("nomeCliente").getAsString();
        } else if (jsonResponse.has("descricao")) {
            mensagemRecebida = jsonResponse.get("descricao").getAsString();
        }

        Assert.assertTrue("A mensagem de erro recebida não está na lista das esperadas.",
                mensagensEsperadas.contains(mensagemRecebida));
    }

    @E("que o arquivo de contrato esperado é o {string}")
    public void queOArquivoDeContratoEsperadoÉO(String contract) throws IOException {
        cadastroAgendamentoService.setContract(contract);
    }

    @Então("a resposta da requisição deve estar em conformidade com o contrato selecionado")
    public void aRespostaDaRequisiçãoDeveEstarEmConformidadeComOContratoSelecionado() throws IOException {
        Set<ValidationMessage> validateResponse = cadastroAgendamentoService.validateResponseAgainstSchema();
        Assert.assertTrue("O contrato está inválido. Erros encontrados: " + validateResponse, validateResponse.isEmpty());
    }
}
