package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    public int getMonthStart() {
        return monthStart;
    }

    public void setMonthStart(int monthStart) {
        this.monthStart = monthStart;
    }

    public int getDayStart() {
        return dayStart;
    }

    public void setDayStart(int dayStart) {
        this.dayStart = dayStart;
    }

    public int getMonthEnd() {
        return monthEnd;
    }

    public void setMonthEnd(int monthEnd) {
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

    public int getParticipantsInvolved() {
        return participantsInvolved;
    }

    public void setParticipantsInvolved(int participantsInvolved) {
        this.participantsInvolved = participantsInvolved;
    }

    public int getYearOfArticle() {
        return yearOfArticle;
    }

    public void setYearOfArticle(int yearOfArticle) {
        this.yearOfArticle = yearOfArticle;
    }

    public int getMonthOfArticle() {
        return monthOfArticle;
    }

    public void setMonthOfArticle(int monthOfArticle) {
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

    public String getCaseOfDispute() {
        return caseOfDispute;
    }

    public void setCaseOfDispute(String caseOfDispute) {
        this.caseOfDispute = caseOfDispute;
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

    public String getCompanyNames() {
        return companyNames;
    }

    public void setCompanyNames(String companyNames) {
        this.companyNames = companyNames;
    }

    public File getArticleUpload() {
        return articleUpload;
    }

    public void setArticleUpload(File article) {
        this.articleUpload = article;
    }

    public String getAuthorInformation() {
        return authorInformation;
    }

    public void setAuthorInformation(String authorInformation) {
        this.authorInformation = authorInformation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Occupation> getOccupations() {
        return occupations;
    }

    public void setOccupations(List<Occupation> occupations) {
        this.occupations = occupations;
    }

    public String getStrikeIdentities() {
        return strikeIdentities;
    }

    public void setStrikeIdentities(String strikeIdentities) {
        this.strikeIdentities = strikeIdentities;
    }

    public String getStrikeDefinitions() {
        return strikeDefinitions;
    }

    public void setStrikeDefinitions(String strikeDefinitions) {
        this.strikeDefinitions = strikeDefinitions;
    }

//    public List<Sector> getSectors() {
//        List<Sector> temps = new ArrayList<>();
//        temps.add(new Sector("Bleh"));
//        temps.add(new Sector("Blah"));
//        temps.add(new Sector("Bloh"));
//        return sectors;
//    }
//
//    public void setSectors(List<Sector> sectors) {
//        this.sectors = sectors;
//    }

    @ManyToMany(cascade = CascadeType.ALL)
    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    @Id
    public Long id;
    private int yearStart,
                monthStart,
                dayStart,
                monthEnd,
                dayEnd,
                duration,
                daysLost,
                participantsInvolved,
                yearOfArticle,
                monthOfArticle,
                dayOfArticle;
    private String   country,
                    location,
                    labourRelation,
                    workersSituation,
                    companyNames,
                    companyOwnership,
                    companyOwnershipSituated,
                    companiesInvolved,
                    typeOfAction,
                    typeOfOrganisation,
                    dominantGender,
                    caseOfDispute,
                    natureOfStrike,
                    outcomeOfStrike,
                    description,
                    authorInformation,
                    source,
                    strikeIdentities,
                    strikeDefinitions;
    private File articleUpload;
    private List<Sector> sectors = new ArrayList<>();
    private List<Occupation> occupations = new ArrayList<>();


    public Strike()
    {
    }

    public static Finder<Integer, Strike> find = new Finder<>(Strike.class);

    public static List<Strike> getAllStrikes()
    {
        List<Strike> strikes = Ebean.find(Strike.class).findList();
        List<Sector> sectors = Ebean.find(Sector.class).findList();
        return strikes;
    }
}