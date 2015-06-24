/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainsrc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import mainsrc.datatypes.applications.PrivateAssembler;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import mainsrc.datatypes.applications.Application;
import mainsrc.datatypes.applications.Assembler;
import org.junit.rules.TemporaryFolder;


/**
 *
 * @author Mark
 * 
 * Created to pull the list (in form of a .yaml-file) of the Applikations from 
 *the url given in the mainsrc.Constants and convert this list in a Java-Object. 
 */
public class YamlInparse {
 
    private URL url;
    private File localFile;
    private String localPath;
    
    // for updates
    private InputStream inStream;
    private StringBuilder bsb;
    private BufferedReader in;
    private FileWriter writer;
    
    
    // for parsing
    private String yamlString;
    private YAMLFactory factory;
    private JsonParser parser;
    private ArrayList<PrivateAssembler> privateAssemblers;
    private List assembler;

  

    
    
    public YamlInparse(){
        this.localPath = Constants.LOCAL_FILE_NAME;
        this.privateAssemblers = new <PrivateAssembler>ArrayList();
        this.assembler = new LinkedHashMap();
        this.yamlString = null;
       
    }
    
    public void parseAtom(){
        try {
            if(this.yamlString== null){
                byte[] encoded = Files.readAllBytes(Paths.get(this.localPath));
                this.yamlString = new String(encoded, StandardCharsets.UTF_8);
            }
            this.factory = new YAMLFactory();
            this.parser = factory.createParser(this.yamlString); // don't be fooled by method name...
            JsonToken token;
            int index = 0;
            String currentFieldName = null;
            String currentFieldValue = null;
            PrivateAssembler assembler =null;
                while ((token = this.parser.nextToken()) != null) {
                    System.out.println(this.parser.getCurrentName() + " "+ this.parser.getValueAsString());
                    
                    if(this.parser.getCurrentName() != null & this.parser.getCurrentTokenId() != 2){
                        if (this.parser.getCurrentName().equals("image") && !currentFieldName.equals("image")){ //sometimes "image" is shown multiple times
                            index++;
                            this.privateAssemblers.add(new PrivateAssembler(index));
                            System.out.println("    new Assembler with index "+index + " and currentFieldName " +currentFieldName );
                            
                        }
                        currentFieldName =this.parser.getCurrentName();
                    }
                    currentFieldValue = this.parser.getValueAsString();
                    if(currentFieldValue!= null){
                        if(currentFieldName.equals("dockerhub")){
                             this.privateAssemblers.get(index-1).setName(currentFieldValue);
                        }
                        else if(currentFieldName.equals("repo")){
                            this.privateAssemblers.get(index-1).setRepository(currentFieldValue);
                        }
                        else if(currentFieldName.equals("source")){
                            this.privateAssemblers.get(index-1).setSource(currentFieldValue);
                        }
                        else if(currentFieldName.equals("pmid")){
                            this.privateAssemblers.get(index-1).setPmid(currentFieldValue);
                        }
                        else if(currentFieldName.equals("homepage")){
                            this.privateAssemblers.get(index-1).setHomepage(currentFieldValue);
                        }
                        else if(currentFieldName.equals("mailing_list")){
                            this.privateAssemblers.get(index-1).setMailing_list(currentFieldValue);
                        }
                        else if(currentFieldName.equals("description")){
                            this.privateAssemblers.get(index-1).setDescription(currentFieldValue);
                        }
                        else if(currentFieldName.equals("tasks")){
                            this.privateAssemblers.get(index-1).addTasks(currentFieldValue);
                        }
                        
                    }   
                }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(YamlInparse.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
                Logger.getLogger(YamlInparse.class.getName()).log(Level.SEVERE, null, ex);
        }
//        
        
    }
    
    public void parse(){ 
        try {
            if(this.yamlString== null){
                byte[] encoded = Files.readAllBytes(Paths.get(this.localPath));
                this.yamlString = new String(encoded, Charset.defaultCharset());
                if(this.yamlString.length()==0){
                    this.updateFile();
                }
            }
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Application app = mapper.readValue(yamlString, mainsrc.datatypes.applications.Application.class);
            this.assembler = app.getAssemblers();
        } catch (IOException ex) {
            Logger.getLogger(YamlInparse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void listAllAssemblers(){
        System.out.println("---");
        System.out.println("assemblers:");
        this.assembler.toString();
    }
    
    public void setlocalPath(String path){
        this.localPath = path;
    }
    
    public void updateFile(){
        
        //adjusting the input and ouput
        try {
            this.url = new URL(Constants.INPUT_FILE_URL);
        } catch (MalformedURLException ex) {
            Logger.getLogger(YamlInparse.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        this.localFile = new File(this.localPath);
        if (!this.localFile.exists()) {
            try {
                this.localFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(YamlInparse.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.localPath = this.localFile.getAbsolutePath();
        
        // reading and writing
        this.bsb = new StringBuilder();
        String line = null;
        try {
            
            this.inStream = this.url.openStream();
            this.in = new BufferedReader(new InputStreamReader(this.inStream));
            this.writer = new FileWriter(this.localFile, false);
            while ((line = this.in.readLine()) != null) {
                this.bsb.append(line);
                this.bsb.append(System.getProperty("line.separator"));
            }
            this.yamlString = this.bsb.toString();
            this.writer.write(this.yamlString);
            this.in.close();
            this.writer.close();
            
            
        } catch (IOException ex) {
            Logger.getLogger(YamlInparse.class.getName()).log(Level.SEVERE, null, ex);
        } 
//        System.out.println("DONE - Path is "+this.localFile.toPath());
    }
    
    private String getAssemblerString(Assembler ass){
        String n = System.getProperty("line.separator");
        StringBuilder taskLister = new StringBuilder();
        return new String(
                "   image:"+n+
                "       dockerhub: " +ass.getImage().getDockerhub() +n+
                "       repo: " + ass.getImage().getRepo() +n+
                "       source: "+ ass.getImage().getSource() + n+ 
                "   pmid: "+ass.getPmid() +n+
                "   homepage: "+ ass.getHomepage() + n+
                "   description:" + ass.getDescription() +n+
                "   tasks: " +n + ass.getTasks().toString()
        );
    }
    
}
