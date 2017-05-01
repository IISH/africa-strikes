package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Igor on 11/4/2016.
 */
@Entity
public class Strike extends Model{

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getYearStart() {
        return yearStart;
    }

    public void setYearStart(int yearStart) {
        this.yearStart = yearStart;
    }

    public String getMonthStart() {
        return monthStart;
    }

    public void setMonthStart(String monthStart) {
        this.monthStart = monthStart;
    }

    public int getDayStart() {
        return dayStart;
    }

    public void setDayStart(int dayStart) {
        this.dayStart = dayStart;
    }

    public int getYearEnd() {
        return yearEnd;
    }

    public void setYearEnd(int yearEnd) {
        this.yearEnd = yearEnd;
    }

    public String getMonthEnd() {
        return monthEnd;
    }

    public void setMonthEnd(String monthEnd) {
        this.monthEnd = monthEnd;
    }

    public int getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(int dayEnd) {
        this.dayEnd = dayEnd;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDaysLost() {
        return daysLost;
    }

    public void setDaysLost(int daysLost) {
        this.daysLost = daysLost;
    }

    public String getParticipantsInvolved() {
        return participantsInvolved;
    }

    public void setParticipantsInvolved(String participantsInvolved) {
        this.participantsInvolved = participantsInvolved;
    }

    public int getYearOfArticle() {
        return yearOfArticle;
    }

    public void setYearOfArticle(int yearOfArticle) {
        this.yearOfArticle = yearOfArticle;
    }

    public String getMonthOfArticle() {
        return monthOfArticle;
    }

    public void setMonthOfArticle(String monthOfArticle) {
        this.monthOfArticle = monthOfArticle;
    }

    public int getDayOfArticle() {
        return dayOfArticle;
    }

    public void setDayOfArticle(int dayOfArticle) {
        this.dayOfArticle = dayOfArticle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLabourRelation() {
        return labourRelation;
    }

    public void setLabourRelation(String labourRelation) {
        this.labourRelation = labourRelation;
    }

    public String getWorkersSituation() {
        return workersSituation;
    }

    public void setWorkersSituation(String workersSituation) {
        this.workersSituation = workersSituation;
    }

    public String getCompanyOwnership() {
        return companyOwnership;
    }

    public void setCompanyOwnership(String companyOwnership) {
        this.companyOwnership = companyOwnership;
    }

    public String getCompanyOwnershipSituated() {
        return companyOwnershipSituated;
    }

    public void setCompanyOwnershipSituated(String companyOwnershipSituated) {
        this.companyOwnershipSituated = companyOwnershipSituated;
    }

    public String getCompaniesInvolved() {
        return companiesInvolved;
    }

    public void setCompaniesInvolved(String companiesInvolved) {
        this.companiesInvolved = companiesInvolved;
    }

    public String getTypeOfAction() {
        return typeOfAction;
    }

    public void setTypeOfAction(String typeOfAction) {
        this.typeOfAction = typeOfAction;
    }

    public String getTypeOfOrganisation() {
        return typeOfOrganisation;
    }

    public void setTypeOfOrganisation(String typeOfOrganisation) {
        this.typeOfOrganisation = typeOfOrganisation;
    }

    public String getDominantGender() {
        return dominantGender;
    }

    public void setDominantGender(String dominantGender) {
        this.dominantGender = dominantGender;
    }

    public String getNatureOfStrike() {
        return natureOfStrike;
    }

    public void setNatureOfStrike(String natureOfStrike) {
        this.natureOfStrike = natureOfStrike;
    }

    public String getOutcomeOfStrike() {
        return outcomeOfStrike;
    }

    public void setOutcomeOfStrike(String outcome) {
        this.outcomeOfStrike = outcome;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorInformation() {
        return authorInformation;
    }

    public void setAuthorInformation(String authorInformation) {
        this.authorInformation = authorInformation;
    }

    public String getGeographicalContext() {
        return geographicalContext;
    }

    public void setGeographicalContext(String geographicalContext) {
        this.geographicalContext = geographicalContext;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    public List<OccupationHisco> getHiscoOccupations() {
        return hiscoOccupations;
    }

    public void setHiscoOccupations(List<OccupationHisco> hiscoOccupations) {
        this.hiscoOccupations = hiscoOccupations;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    public List<IdentityElement> getIdentityElements() {
        return identityElements;
    }

    public void setIdentityElements(List<IdentityElement> identityElements) {
        this.identityElements = identityElements;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    public List<StrikeDefinition> getStrikeDefinitions() {
        return strikeDefinitions;
    }

    public void setStrikeDefinitions(List<StrikeDefinition> strikeDefinitions) {
        this.strikeDefinitions = strikeDefinitions;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    public List<CauseOfDispute> getCauseOfDisputes() {
        return causeOfDisputes;
    }

    public void setCauseOfDisputes(List<CauseOfDispute> causeOfDisputes) {
        this.causeOfDisputes = causeOfDisputes;
    }

    public String getCompanyNames() {
        return String.join(",", companyNames.stream().map(c -> c.companyNameText).collect(Collectors.toList()));
    }

    public void setCompanyNames(String companies) {

        List<String> companyNames2 = Arrays.asList(companies.split(","));
        companyNames.addAll(companyNames2.stream().map(CompanyName::new).collect(Collectors.toList()));
    }

    public String getOccupations() {
        return String.join(",", occupations.stream().map(o -> o.occupationText).collect(Collectors.toList()));
    }

    public void setOccupations(String newOccupations) {
        List<String> occupationsToSave = Arrays.asList(newOccupations.split(","));
        occupations.addAll(occupationsToSave.stream().map(Occupation::new).collect(Collectors.toList()));
    }

    public String getIdentityDetails() {
        return String.join(",", identityDetails.stream().map(i -> i.identityDetailText).collect(Collectors.toList()));
    }

    public void setIdentityDetails(String identities) {
        List<String> identitiesToSave = Arrays.asList(identities.split(","));
        identityDetails.addAll(identitiesToSave.stream().map(IdentityDetail::new).collect(Collectors.toList()));
    }

    @OneToOne(cascade = CascadeType.ALL)
    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Id
    public Long id;
    private int yearStart,
                dayStart,
                yearEnd,
                dayEnd,
                duration,
                daysLost,
                yearOfArticle,
                dayOfArticle;
    private String  country,
                    location,
                    monthStart,
                    monthEnd,
                    monthOfArticle,
                    labourRelation,
                    workersSituation,
                    companyOwnership,
                    companyOwnershipSituated,
                    companiesInvolved,
                    participantsInvolved,
                    typeOfAction,
                    typeOfOrganisation,
                    dominantGender,
                    natureOfStrike,
                    outcomeOfStrike,
                    description,
                    authorInformation,
                    source,
                    geographicalContext;
    private Article article;
    private List<Sector> sectors = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<Occupation> occupations = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<CompanyName> companyNames = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<IdentityDetail> identityDetails = new ArrayList<>();
    private List<CauseOfDispute> causeOfDisputes = new ArrayList<>();
    private List<OccupationHisco> hiscoOccupations = new ArrayList<>();
    private List<IdentityElement> identityElements = new ArrayList<>();
    private List<StrikeDefinition> strikeDefinitions = new ArrayList<>();
    private Boolean checked;

    public Strike()
    {
    }

    /**
     * Method to find the strikes in the database
     */
    public static Finder<Integer, Strike> find = new Finder<>(Strike.class);

    /**
     * gets all strikes from the database
     * @return a List of strikes List<Strikes>
     */
    public static List<Strike> getAllStrikes()
    {
        List<Strike> strikes = Ebean.find(Strike.class).findList();
        return strikes;
    }

    /**
     * gets all strikes by username from the database
     * @param user the username to look for
     * @return a List of strikes List<Strikes>
     */
    public static List<Strike> getAllStrikesByUser(String user){
        List<Strike> strikes = Ebean.find(Strike.class).findList();
        List<Strike> strikesToSend = new ArrayList<>();
        for(Strike s : strikes){
            if(s.getAuthorInformation().equals(user)){
                strikesToSend.add(s);
            }
        }
        return strikesToSend;
    }

    /**
     * gets all the strikes from the database as an array
     * @return an array of strikes
     */
    public static Strike[] getAllStrikesAsArray()
    {
        List<Strike> strikes = Ebean.find(Strike.class).findList();
        Strike[] strikesToSend = new Strike[ strikes.size()];
        strikes.toArray(strikesToSend);
        return strikesToSend;
    }

    /**
     * gets ids from strikes by username from the database
     * @param user the username to look for
     * @return an array of ids from strikes Long[]
     */
    public static Long[] getStrikeIdsByUser(String user){
        List<Strike> objects = Ebean.find(Strike.class).findList();
        List<Long> strikesIds = new ArrayList<>();
        for(Strike obj : objects){
            if(obj.getAuthorInformation().equals(user)){
                strikesIds.add(obj.id);
            }
        }
        Long[] strikesToSend = new Long[strikesIds.size()];
        strikesIds.toArray(strikesToSend);
        return strikesToSend;
    }

    /**
     * gets ids from all the strike from the database
     * @return an array of ids from strikes Long[]
     */
    public static Long[] getAllStrikesIds(){
        List<Object> objects = Ebean.find(Strike.class).findIds();
        List<Long> strikesIds = new ArrayList<>();
        for (Object obj : objects) {
            strikesIds.add((Long)obj);
        }
        Collections.sort(strikesIds);
        Long[] strikesToSend = new Long[strikesIds.size()];
        strikesIds.toArray(strikesToSend);
        return strikesToSend;
    }
}