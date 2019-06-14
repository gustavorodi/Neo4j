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
    public  Integer x = 0;
    private Boolean valida = true;
    private String complemento;
    private static Integer  pontoDivergente;
    private static String   nomeDoNovoContato;
    private static Integer  idadeDoNovoContato;
    
    
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

          System.out.println("Olá, qual operação deseja fazer ?");
        System.out.println("1 - Adicionar nova pessoa na lista. ");
        System.out.println("2 - Remover pessoa da lista.");
        System.out.println("3 - Listar as pessoas da sua lista.");
        Integer pontoDivergente = copia.nextInt();


        switch (pontoDivergente){
            case 1:
                System.out.println("Digite o nome da pessoa: ");
                String nomeAdd = copia.next();
                System.out.println("Qual a idade do "+nomeAdd+"?");
                Integer idade = copia.nextInt();
                System.out.println("Qual o numero de celular do "+nomeAdd+"?");
                Integer ncelular = copia.nextInt();
                
                greeter.adicionar( nomeAdd,idade, ncelular );
                break;
            case 2:
                System.out.println("Digite o nome da Pessoa que deseja deletar: ");
                String nomeDel = copia.next();
                System.out.println("Tem certeza que deseja deletar "+nomeDel+" ? (Sim/não)");
                String contradicao = copia.next();
                if("Sim".equals(contradicao)){
                    greeter.deletar(nomeDel);
                }
                break;
            case 3:
                   greeter.listar();
                break;
            default:

                break;
        }
  }
    
    public void adicionar(String nome, Integer idade, Integer ncelular) {
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
        
        public void listar() {
        Session session = driver.session();
        
        String greeting = session.writeTransaction( new TransactionWork<String>() {
            @Override
            public String execute(Transaction tx) {
                StatementResult result = tx.run("" +
                    "MATCH (n:contatos) "+
                    "RETURN n");

                return result.single().get(0).asString();
            }
        });
        
        System.out.println(greeting + ", ");
    } 
        
        
        
        
}

