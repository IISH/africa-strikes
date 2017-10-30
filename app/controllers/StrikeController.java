package controllers;

import com.avaje.ebean.Ebean;
import com.typesafe.config.ConfigFactory;
import models.*;
import org.apache.commons.lang3.StringUtils;
import play.libs.Yaml;
import play.mvc.Http;
import play.Logger;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Igor on 1/31/2017.
 */
public class StrikeController {
    private static List<String> sourceData;
    private static List<String> countryData;
    private static List<String> labourRelations;
    private static List<String> months;
    private static List<String> numberOfParticipants;
    private boolean isFirstLoad = true;
    final Logger.ALogger logger = Logger.of(this.getClass());

    /**
     * Checks whether the application has been loaded for the first time.
     * When first time, it loads data from Yaml files
     */
    public void checkFirstLoad(){
        if(isFirstLoad){
            loadYamlFiles();
            isFirstLoad = false;
        }
    }

    /**
     * Loads the information from the Yaml files
     */
    public void loadYamlFiles(){
        sourceData = (List<String>) Yaml.load("source-data.yml");
        countryData = (List<String>) Yaml.load("country-data.yml");
        labourRelations = (List<String>) Yaml.load("labour-relations.yml");
        months = (List<String>) Yaml.load("months.yml");
        numberOfParticipants = (List<String>) Yaml.load("number-of-participants.yml");
    }

    /**
     * Saves the given List of Yaml files to the database
     * @param yamlList List of Yaml files
     * @param <T> the Yaml file
     */
    public <T> void saveYamlFileToDatabase(List<T> yamlList)
    {
        try {
            for (int i = 0; i < yamlList.size(); i++) {
                Ebean.save(yamlList.get(i));
            }
        }
        catch (Exception e) {
            Logger.error("Exception loading Yaml files into database " + e);
        }
    }

    /**
     * Handles the upload of the Article provided with the strike entered.
     * @param article the article provided
     * @param strike the strike belonging to the article
     */
    public void handleStrikeArticle(Http.MultipartFormData.FilePart<File> article, Strike strike){
        if(article.getFilename().length() > 0) {
            String[] temp = article.getFilename().split("\\.");
            String extension = temp[temp.length - 1].toString();

            // creating the file with the correct path
            File articleFile = null;
            try {
                articleFile = File.createTempFile("article-", "." + extension, new File(ConfigFactory.load().getString("articleFilePath")));
            } catch (Exception e) {
                logger.error("Exception creating the initial file " + e);
            }

            // Checks if the file has an image extension
            if (Pattern.compile("((?i)(jpg|png|gif|bmp)$)").matcher(extension).matches()) {
                BufferedImage image = null;
                try {
                    image = ImageIO.read(article.getFile());
                    ImageIO.write(image, extension, articleFile);
                } catch (Exception e) {
                    logger.error("Exception checking image extension " + e);
                }
            }
            // Checks if the file has a pdf or tif extension
            else if (Pattern.compile("((?i)(pdf|tiff|tif)$)").matcher(extension).matches()) {
                try {
                    FileInputStream fs = new FileInputStream(article.getFile());
                    int b;
                    FileOutputStream os = new FileOutputStream(articleFile);
                    while ((b = fs.read()) != -1) {
                        os.write(b);
                    }
                    os.close();
                    fs.close();
                } catch (Exception e) {
                    logger.error("Exception checking pdf or tiff extension " + e);
                }
            }
            strike.setArticle(new Article(articleFile.getName()));
        }
    }

    /**
     * Handles the mapping of the data for the strike entered
     * @param strike the strike entered
     * @param body the information for the strike
     */
    protected void handleStrikeMapping(Strike strike, Http.MultipartFormData body){
        // --------------------------------------------------------------------------------- \\
        // Maps the sectors given by the form and puts them in the sectors list of the Strike
        Map<?, Sector> sectorMap = Sector.find.findMap();
        strike.setSectors(mapSelectedOptionsToTheStrike(body, strike, "sectors[]", sectorMap));

        // --------------------------------------------------------------------------------- \\
        // Maps the occupations given by the form and puts them in the occupations list of the Strike
        Map<?, OccupationHisco> occupationHiscoMap = OccupationHisco.find.findMap();
        strike.setHiscoOccupations(mapSelectedOptionsToTheStrike(body, strike, "hiscoOccupations[]", occupationHiscoMap));

        // --------------------------------------------------------------------------------- \\
        // Maps the causeOfDisputes given by the form and puts them in the causeOfDisputes list of the Strike
        Map<?, CauseOfDispute> causeOfDisputeMap = CauseOfDispute.find.findMap();
        strike.setCauseOfDisputes(mapSelectedOptionsToTheStrike(body, strike, "causeOfDisputes[]", causeOfDisputeMap));

        // --------------------------------------------------------------------------------- \\
        // Maps the identityElements given by the form and puts them in the identityElements list of the Strike
        Map<?, IdentityElement> identityElementMap = IdentityElement.find.findMap();
        strike.setIdentityElements(mapSelectedOptionsToTheStrike(body, strike, "identityElements[]", identityElementMap));

        // --------------------------------------------------------------------------------- \\
        // Maps the strikeDefinitions given by the form and puts them in the strikeDefinitions list of the Strike
        Map<?, StrikeDefinition> strikeDefinitionMap = StrikeDefinition.find.findMap();
        strike.setStrikeDefinitions(mapSelectedOptionsToTheStrike(body, strike, "strikeDefinitions[]", strikeDefinitionMap));

        // --------------------------------------------------------------------------------- \\
        // Maps the strikeDefinitions given by the form and puts them in the strikeDefinitions list of the Strike
        Map<?, Label> labelMap = Label.find.findMap();
        String[] ids = (String[]) body.asFormUrlEncoded().get("strikeLabels[]");
        Long idCounter = (long)labelMap.size();
        for(int i = 0; i < ids.length; i++){
            if(!StringUtils.isNumeric(ids[i])){
                idCounter += 1;
                Label.addLabelToDatabase(idCounter, ids[i]);
                ids[i] = Long.toString(idCounter);
            }
        }
        Map<?, Label> labelMapNew = Label.find.findMap();
        strike.setLabels(mapSelectedLabelsToTheStrike(body, strike, "strikeLabels[]", labelMapNew, ids));
    }

    protected  <T> List<T> mapSelectedLabelsToTheStrike(Http.MultipartFormData body, Strike strike, String name, Map<?, T> input, String[] ids)
    {
        try{
            return Stream.of(ids)
                    .map(id -> input.getOrDefault(new Long(id), null))
                    .collect(Collectors.toList());
        }
        catch (Exception e){
            // Create default sector to be given when no sector selected
            String[] idsAlt = {"1"};
            return Stream.of(idsAlt)
                    .map(id -> input.getOrDefault(new Long(id), null))
                    .collect(Collectors.toList());
        }
    }


    /**
     * Maps the information for the strike in the correct format
     * @param body the information
     * @param strike the strike entered
     * @param name the name of the variable
     * @param input the map to input
     * @param <T>
     * @return a Stream
     */
    protected  <T> List<T> mapSelectedOptionsToTheStrike(Http.MultipartFormData body, Strike strike, String name, Map<?, T> input)
    {
        try{
            String[] ids = (String[]) body.asFormUrlEncoded().get(name);
            return Stream.of(ids)
                    .map(id -> input.getOrDefault(new Long(id), null))
                    .collect(Collectors.toList());
        }
        catch (Exception e){
            // Create default sector to be given when no sector selected
            String[] ids = {"1"};
            return Stream.of(ids)
                    .map(id -> input.getOrDefault(new Long(id), null))
                    .collect(Collectors.toList());
        }
    }

    public static List<String> getSourceData() {
        return sourceData;
    }

    public static List<String> getCountryData() {
        return countryData;
    }

    public static List<String> getLabourRelations() {
        return labourRelations;
    }

    public static List<String> getMonths() {
        return months;
    }

    public static List<String> getNumberOfParticipants() {
        return numberOfParticipants;
    }
}
