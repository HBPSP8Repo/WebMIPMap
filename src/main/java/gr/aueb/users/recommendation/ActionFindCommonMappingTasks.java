/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aueb.users.recommendation;

import static gr.aueb.controllers.MappingController.user;
import gr.aueb.users.ActionGetUsers;
import gr.aueb.users.recommendation.mappingmodel.Field;
import gr.aueb.users.recommendation.mappingmodel.MappingScenario;
import gr.aueb.users.recommendation.mappingmodel.Schema;
import gr.aueb.users.recommendation.mappingmodel.Table;
import it.unibas.spicy.persistence.DAOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author ioannisxar
 */
public class ActionFindCommonMappingTasks {
    
    private String user, mappingName;
    
    public ActionFindCommonMappingTasks(String user, String mappingName){
        this.user = user;
        this.mappingName = mappingName;
    }
    
    public void findCommonScenarions() throws DAOException, IOException{
        OpenMappingScenario scenarioToMatch = new OpenMappingScenario(user, mappingName);
        Schema sourceSchemaToCheck = scenarioToMatch.getScenarioSchema("source", "private");
        Schema targetSchemaToCheck = scenarioToMatch.getScenarioSchema("target", "private");
        ArrayList<MappingScenario> trustedUserPublicMappings = trustedMappingsToCheck();
        for(MappingScenario scenario: trustedUserPublicMappings){
            System.out.println(scenario.getUserName());
            System.out.println(scenario.getMappingTaskName());
            scenario.getSource().printSchema();
            scenario.getTarget().printSchema();
        }
    }
    
    private ArrayList<MappingScenario> trustedMappingsToCheck() throws DAOException, IOException{
        ArrayList<MappingScenario> trustedUserPublicMappings = new ArrayList<>();
        ActionGetUsers actionGetUsers = new ActionGetUsers();
        actionGetUsers.performAction(user);
        JSONObject outputObject = actionGetUsers.getJSONObject();
        JSONArray trustedUsers = (JSONArray) outputObject.get("trustUsers");
        Iterator<JSONObject> iterator = trustedUsers.iterator();
        while (iterator.hasNext()) {
            JSONObject innerObject = iterator.next();
            JSONArray publicTasks = (JSONArray) innerObject.get("publicTasks");
            Iterator<JSONObject> publicTasksIterator = publicTasks.iterator();
            while (publicTasksIterator.hasNext()) {
                String userName = (String) innerObject.get("userName");
                String mappingTaskName = (String) publicTasksIterator.next().get("taskName");
                OpenMappingScenario scenario = new OpenMappingScenario(userName, mappingTaskName);
                Schema sourceSchema = scenario.getScenarioSchema("source", "public");
                Schema targetSchema = scenario.getScenarioSchema("target", "public");
                trustedUserPublicMappings.add(new MappingScenario(userName, mappingTaskName, sourceSchema, targetSchema));
            }
        }
        return trustedUserPublicMappings;
    }
    
}
