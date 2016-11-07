package models;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Igor on 11/4/2016.
 */

public class Strike{

    public int id,
                yearStart,
                monthStart,
                dayStart,
                yearEnd,
                monthEnd,
                dayEnd,
                duration,
                daysLost,
                participantsInvolved,
                yearOfArticle,
                monthOfArticle,
                dayOfArticle;
    public String   country,
                    location,
                    labourRelation,
                    workersSituation,
                    companyOwnership,
                    companyOwnershipSituated,
                    companiesInvolved,
                    typeOfAction,
                    typeOfOrganisation,
                    dominantGender,
                    caseOfDispute,
                    natureOfStrike,
                    outcome,
                    description;
    public List<String> sources, sectors, occupations, occupationsHisco, companies, strikeIdentity, strikeDefinitions;

    public Strike()
    {

    }

    public static List<String> sourceOptions()
    {
        List<String> tmp = new ArrayList<String>();

        tmp.add("Sierra Leone Weekly News");
        tmp.add("Times of East Africa");
        tmp.add("Uganda Herald");

        return tmp;
    }
}
