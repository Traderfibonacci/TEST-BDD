# language: pt
Funcionalidade: Cadastro de novo agendamento
  Como usuário da API
  Quero cadastrar um novo agendamento
  Para que o registro seja salvo corretamente no sistema

  Cenário: Cadastro bem-sucedido de agendamento
    Dado que eu tenha os seguintes dados da agendamento:
      | campo          | valor         |
      | nomeCliente    | Ana Silva     |
      | dataAgendamento| 2024-10-30    |
      | tipoMaterial   | Reciclavel    |
      | descricao      | Coleta de plastico   |
    Quando eu enviar a requisição para o endpoint "/api/agendamento" de cadastro de agendamento
    Então o status code da resposta deve ser 201

  Cenário: Cadastro de agendamento com dados inválidos
    Dado que eu tenha os seguintes dados da agendamento:
      | campo          | valor        |
      | nomeCliente    |              |
      | dataAgendamento| 2024-10-30   |
      | tipoMaterial   | Reciclavel   |
      | descricao      | Coleta de plastico   |
    Quando eu enviar a requisição para o endpoint "/api/agendamento" de cadastro de agendamento
    Então o status code da resposta deve ser 400
    E a resposta deve conter a mensagem de erro "Nome do Cliente é Obrigatório!"

  Cenário: Cadastro de agendamento com descrição vazia
    Dado que eu tenha os seguintes dados da entrega:
      | campo          | valor         |
      | nomeCliente    | Ana Silva     |
      | dataAgendamento| 2024-10-30    |
      | tipoMaterial   | Reciclavel    |
      | descricao      |               |
    Quando eu enviar a requisição para o endpoint "/api/agendamento" de cadastro de agendamento
    Então o status code da resposta deve ser 400
    E a resposta deve conter uma das seguintes mensagens de erro:
      | Descrição do Material é Obrigatório! |
      | Campo não pode estar Vazio,No maximo 20 caracteres |

  Cenário: Validar contrato do cadastro bem-sucedido de agendamento
    Dado que eu tenha os seguintes dados da entrega:
      | campo          | valor         |
      | nomeCliente    | Ana Silva     |
      | dataAgendamento| 2024-10-30    |
      | tipoMaterial   | Reciclavel    |
      | descricao      | Coleta de plastico   |
    Quando eu enviar a requisição para o endpoint "/api/agendamento" de cadastro de agendamento
    Então o status code da resposta deve ser 201
    E que o arquivo de contrato esperado é o "Cadastro bem-sucedido de agendamento"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado


