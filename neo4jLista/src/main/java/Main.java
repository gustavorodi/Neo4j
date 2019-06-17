/*
 * @author Gustavo
 */

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Scanner;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import static org.neo4j.driver.v1.Values.parameters;

public class Main implements AutoCloseable {
    private final Driver driver;

    public Main( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }
    
     public void close() throws Exception
    {
        driver.close();
    }
   
    public static void main(String[] args)  throws Exception 
    { 
        Main greeter = new Main( "bolt://localhost:7687", "neo4j", "adm" );
       
        Scanner copia = new Scanner(System.in);

      if(!greeter.validarLista()){
            greeter.criarLista();   
      }
        
        System.out.println("Olá, qual operação deseja fazer ?");
        System.out.println("1 - Adicionar nova pessoa na lista. ");
        System.out.println("2 - Remover pessoa da lista.");
        System.out.println("3 - Mostar o numero.");
        System.out.println("4 - Sair");
        Integer pontoDivergente = copia.nextInt();
            

        switch (pontoDivergente){
            case 1:
                System.out.println("Digite o nome da pessoa: ");
                String nomeAdd = copia.next();
                System.out.println("Qual a idade do "+nomeAdd+"?");
                Integer idade = copia.nextInt();
                System.out.println("Qual o numero de celular do "+nomeAdd+"?");
                Long ncelular = copia.nextLong();
             
                greeter.adicionar( nomeAdd,idade, ncelular );
                 System.exit(0);
                break;
            case 2:
                System.out.println("Digite o nome da Pessoa que deseja deletar: ");
                String nomeDel = copia.next();
                System.out.println("Tem certeza que deseja deletar "+nomeDel+" ? (Sim/não)");
                String contradicao = copia.next();
                
                if("Sim".equals(contradicao)){
                    greeter.deletar(nomeDel);
                     System.exit(0);
                }
                break;
            case 3:
                   System.out.println("Você quer saber o numero de quem ?");
                    String nomeBus = copia.next();
                    
                   greeter.buscarNumero(nomeBus);
                    System.exit(0);
                break;
                
             case 4:
                 System.exit(0);
                 break;
            default:
                System.err.println("Valor invalido");
                 System.exit(0);

                break;
        }
  }
    
    public void adicionar(String nome, Integer idade, Long ncelular) {
        Session session = driver.session();
        
        String greeting = session.writeTransaction( new TransactionWork<String>() {
            @Override
            public String execute(Transaction tx) {
                StatementResult result = tx.run("" +
                    "MATCH (Lista:Agenda {name: 'Lista_de_Contatos'}) " +
                    "CREATE (Lista)-[rel:HAS]->(person:contatos {name: '" + nome + "', idade: '" + idade + "', numero_celular: '"+ncelular+"'}) " +
                    "RETURN person.name");

                return result.single().get(0).asString();
            }
        });
        
        System.out.println(greeting+" Foi adicionado na lista");
    }
    
    public void deletar(String nome) {
        Session session = driver.session();
        
        Boolean greeting = session.writeTransaction( new TransactionWork<Boolean>() {
            @Override
            public Boolean execute(Transaction tx) {
                StatementResult result = tx.run("" +
                    "MATCH (person:contatos {name:'"+nome+"'})"+
                    " DETACH DELETE person");

                return true;
            }
        });
        
        System.out.println(greeting? nome+" Foi deletado" : "False");
    }
        
        public void buscarNumero(String nome) {
        Session session = driver.session();
        
        String greeting = session.writeTransaction( new TransactionWork<String>() {
            @Override
            public String execute(Transaction tx) {
                StatementResult result = tx.run(""+
                        "MATCH (n:contatos {name:'"+nome+"'})"+
                        " RETURN n.numero_celular");

                return result.single().get(0).asString();
            }
        });
        
        System.out.println("O numero de telefone de " +nome+ " é "+greeting);
    } 
        
        
    public Boolean validarLista() {
        Session session = driver.session();
        
        String greeting = session.writeTransaction( new TransactionWork<String>() {
            @Override
            public String execute(Transaction tx) {
                StatementResult result = tx.run("" +
                     "MATCH (n:Agenda {name: 'Lista_de_Contatos'})"+
                     " RETURN n.name");

                    return result.single().get(0).asString();
            }
        });  
        if("Lista_de_Contatos" == greeting){
            return true;
        }
        return false;

    }
    
        public void criarLista() {
        Session session = driver.session();
        
        String greeting = session.writeTransaction( new TransactionWork<String>() {
            @Override
            public String execute(Transaction tx) {
                StatementResult result = tx.run(""+
                        "CREATE (Lista:Agenda{name: 'Lista_de_Contatos' })"+
                        " RETURN Lista");

                return "Criado";
            }
        });
        
    } 
    
    
}

