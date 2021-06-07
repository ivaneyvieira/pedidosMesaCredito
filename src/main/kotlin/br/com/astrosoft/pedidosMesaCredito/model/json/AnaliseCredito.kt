package br.com.astrosoft.pedidosMesaCredito.model.json

import com.beust.klaxon.Json
import java.time.LocalDateTime

data class AnaliseCredito(val requestIdentification: String,
                          @Json(name = "processId") val processID: String,
                          @Json(name = "prospectId") val prospectID: String,
                          @KlaxonDateTime val requestDateTime: LocalDateTime,
                          val data: Data,
                          val metrics: Metrics) {
  fun toJson() = klaxon.toJsonString(this)

  companion object {
    fun fromJson(json: String) = klaxon.parse<AnaliseCredito>(json)
  }
}

data class Data(val perfilDasPessoas: PerfilDasPessoas,
                val indicadoresGeradosRegra: IndicadoresGeradosRegra,
                val indicadores: Indicadores,
                val interromper: Boolean)

data class Indicadores(val parametros: Parametros)

data class Parametros(val paramConsideraConsultasProprias: Boolean,
                      val paramIdadeMaxima: Long,
                      val paramIdadeMinima: Long,
                      val paramMultiplicadorRenda: Long,
                      val paramNumeroCCFDeriva: Long,
                      val paramNumeroCCFReprova: Long,
                      val paramNumeroConsultasDeriva: Long,
                      val paramNumeroDiasConsultas: Long,
                      val paramNumeroProtestosDeriva: Long,
                      val paramNumeroProtestosReprova: Long,
                      val paramNumeroRestricoesComunsDeriva: Long,
                      val paramNumeroRestricoesComunsReprova: Long,
                      val paramNumeroRestricoesEspeciaisDeriva: Long,
                      val paramNumeroRestricoesEspeciaisReprova: Long,
                      val paramPercentualNomeSimilaridade: Double,
                      val paramPercentualPrejuizo: Long,
                      val paramPercentualVencido: Long,
                      val paramPercentualVencidoAcima90Dias: Long,
                      val paramPercentualVencidoAte90Dias: Long,
                      val paramUtilizaGrafiaEspecial: Boolean,
                      val paramValorProtestosDeriva: Double,
                      val paramValorProtestosReprova: Double,
                      val paramValorRestricoesComunsDeriva: Double,
                      val paramValorRestricoesComunsReprova: Double,
                      val paramValorRestricoesEspeciaisDeriva: Double,
                      val paramValorRestricoesEspeciaisReprova: Double,
                      val paramValorSalarioMinimo: Double,
                      val paramValorVencidoAte90Dias: Long,
                      val paramVerificaAcaoJudicial: Boolean,
                      val paramVerificaAlerta: Boolean,
                      val paramVerificaChequesSustados: Boolean,
                      val paramVerificaConsultas: Boolean,
                      val paramVerificaParticipacao: Boolean,
                      val executarConsultaReceitaFederalPF: Boolean,
                      val executarConsultaSpcBrasilNovoSpcMixMais: Boolean,
                      val executarConsultaSpcBrasilSpc12: Boolean)

data class IndicadoresGeradosRegra(val regrasAplicadas: RegrasAplicadas,
                                   val csvRegras: List<CSVRegra>,
                                   val listaDeGrafias: List<ListaDeGrafia>)

data class CSVRegra(val habilitado: String,
                    val regra: String,
                    val resultadoRegra: String,
                    val nomeAtributo: String,
                    val resultadoAtributo: String,
                    val mensagem: String,
                    val nomeCriterioA: String? = null,
                    val tipoCriterioA: String? = null,
                    val nomeParametroA: String? = null,
                    val tipoParametroA: String? = null,
                    val nomeCriterioB: String? = null,
                    val tipoCriterioB: String? = null)

data class ListaDeGrafia(val nomeEmpresa: String)

data class RegrasAplicadas(val regraIdadeMinima: String,
                           val regraIdadeMaxima: String,
                           val regraSalarioMinimo: String,
                           val biroReceitaFederalPF: String,
                           val regraCPFInexistente: String,
                           val regraDataNascimento: String,
                           val regraObito: String,
                           val regraSituacaoCadastral: String,
                           val regraSimilaridadeNome: String,
                           val biroSPCBrasil: String,
                           val regraNumeroConsultas: String,
                           val regraChequesSustados: String,
                           val regraAlerta: String,
                           val regraAcaoJudicial: String,
                           val regraNumRestricoesEspeciais: String,
                           val regraValorRestricoesEspeciais: String,
                           val regraNumRestricoesComuns: String,
                           val regraValorRestricoesComuns: String,
                           val regraNumProtesto: String,
                           val regraValorProtesto: String,
                           val regraNumCCF: String,
                           val biroBacen: String,
                           val regraCPFInexistenteBacen: String,
                           val regraPercVencidos: String,
                           val regraPercPrejuizo: String,
                           val regraValorVencAte90Dias: String,
                           val regraPercVencAte90Dias: String,
                           val regraPercVencAcima90Dias: String)

data class PerfilDasPessoas(val proponente: Proponente)

data class Proponente(val documento: String,
                      val tipoPessoa: String,
                      val dataNascimento: String,
                      val renda: Double,
                      val classificacao: String,
                      val servicos: Servicos)

data class Servicos(val spcbrasil: Spcbrasil, val receita: Receita)

data class Receita(val situacaoPf: SituacaoPf)

data class SituacaoPf(val id: String,
                      val possuiErro: Boolean,
                      val possuiReaproveitamento: Boolean,
                      val possuiCamposNaoMapeados: Boolean,
                      val dataConsulta: String,
                      val encontrouRegistro: Boolean,
                      val dadosDivergentes: Boolean,
                      val documento: String,
                      val nome: String,
                      val dataNascimento: String,
                      val situacao: String,
                      val dataInscricao: String,
                      val digitoVerificador: String,
                      val dataEmissaoComprovante: String,
                      val horaEmissaoComprovante: String,
                      val codigoControle: String,
                      val possuiObito: Boolean)

data class Spcbrasil(val spc: Spc)

data class Spc(val id: String,
               val possuiErro: Boolean,
               val possuiReaproveitamento: Boolean,
               val possuiCamposNaoMapeados: Boolean,
               val dataConsulta: String,
               val resumoDeAlertas: ResumoDeAlertas,
               val consumidor: Consumidor,
               val protocolo: Protocolo,
               val operador: Operador,
               val restricao: Boolean,
               val data: String,
               val resumoTelefoneConsultado: Resumo,
               val resumoAlertaDocumento: Resumo,
               val resumoSpc: Resumo,
               val resumoChequeLojista: Resumo,
               val resumoAcao: Resumo,
               val resumoCreditoConcedido: Resumo,
               val consultaRealizada: List<ConsultaRealizada>,
               val resumoConsultaRealizada: ResumoConsultaRealizada,
               val resumoInformacaoPoderJudiciario: Resumo,
               val ultimoEnderecoInformado: List<UltimoEnderecoInformado>,
               val rendaPresumidaSpc: RendaPresumidaSpc,
               val resumoRendaPresumidaSpc: ResumoRendaPresumidaSpc)

data class ConsultaRealizada(val origemAssociado: String,
                             val uf: String,
                             val nomeAssociado: String,
                             val dataConsulta: String,
                             val nomeEntidadeOrigem: String)

data class Consumidor(val consumidorPessoaFisica: ConsumidorPessoaFisica)

data class ConsumidorPessoaFisica(val cpf: String,
                                  val cpfRegiaoOrigem: String,
                                  val situacaoCpf: Map<String, Any>,
                                  val estadoRg: String,
                                  val endereco: Endereco,
                                  val telefoneResidencial: Telefone,
                                  val telefoneCelular: Telefone,
                                  val dataNascimento: String,
                                  val email: String,
                                  val idade: Long,
                                  val nome: String,
                                  val nomeMae: String,
                                  val numeroRg: String,
                                  val numeroTituloEleitor: Long,
                                  val sexo: String,
                                  val signo: String)

data class Endereco(val cidade: String,
                    val uf: String,
                    val logradouro: String,
                    val numero: String,
                    val bairro: String,
                    val cep: Long,
                    val complemento: String? = null)

data class Telefone(val numeroDdd: Long, val numero: Long)

data class Operador(val codigo: String, val nome: String)

data class Protocolo(val numero: Long, val digito: Long)

data class RendaPresumidaSpc(val mediana: Double)

data class Resumo(val quantidadeTotal: Long)

data class ResumoConsultaRealizada(val quantidadeTotal: Long,
                                   val dataUltimaOcorrencia: String,
                                   val quantidadeDiasConsultados: Long)

data class ResumoDeAlertas(val quantidadeRestricoes: Long,
                           val valorTotalRestricoes: Double,
                           val resumoPassagens: ResumoPassagens)

data class ResumoPassagens(val quantidadeTotal: Long,
                           val quantidade30Dias: Long,
                           val quantidade31A60Dias: Long,
                           val quantidade61A90Dias: Long)

data class ResumoRendaPresumidaSpc(val quantidadeTotal: Long, val valorTotal: Long)

data class UltimoEnderecoInformado(val endereco: Endereco)

data class Metrics(val executionTime: Long,
                   val totalExecutionTime: Long,
                   val elapsedTimes: List<ElapsedTime>,
                   val result: String,
                   val logs: List<Log>,
                   val bureau: Bureau)

data class Bureau(@Json(name = "spcbrasil-spc") val spcbrasilSpc: Long,

                  @Json(name = "receita-situacaoPf") val receitaSituacaoPf: Long)

data class ElapsedTime(@Json(name = "taskId") val taskID: String,

                       val taskName: String, val elapsedTime: Long)

data class Log(val location: String,
               val message: String,
               val type: String,
               val userName: String,
               val userEmail: String,
               val priorityLevel: Long)
