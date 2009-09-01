package com.rusticisoftware.scormcloud.tool;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.*;

public class ActivityReporter {
    private StringBuilder xml = new StringBuilder();

    public ActivityReporter(){
    }
    
    public String createReport(Document fullRegResults){
        Element docRoot = fullRegResults.getDocumentElement();
        Element rootActivity = getFirstChildByTagName(docRoot, "activity");
        
        xml.delete(0, xml.length());
        addHeaders();                               
        addActivity(rootActivity, 0, 0);
        return xml.toString();
    }
    
    public void addHeaders(){
        xml.append("<div id='column_headers'>");
        xml.append("<div class='headertitle' >Learning Object Name</div>");
        xml.append("<div class='headersatisfied'>Satisfaction</div>");
        xml.append("<div class='headercompleted'>Completion</div>");
        xml.append("<div class='headerattempts'>Attempts</div>");
        xml.append("<div class='headersuspended'>Suspended</div>");
        xml.append("</div>"); 
    }
    
    public void addActivity(Element activity, int actNum, int leftMargin){
        String title = getChildElemText(activity, "title");
        String satisfied = getSatisfiedValue(
                getChildElemText(activity, "progressstatus"),
                getChildElemText(activity, "satisfied"));
        String completed = getCompletionValue(
                getChildElemText(activity, "attemptprogressstatus"),
                getChildElemText(activity, "completed" ));
        String attempts = getChildElemText(activity, "attempts");
        String suspended = getChildElemText(activity, "suspended");
        
        xml.append("<div class='activity'>");
        
        xml.append("<div class='title' style='margin-left:" + leftMargin + "px;'>" + title + "</div>");
        xml.append("<div class='satisfaction " + satisfied + "'>" + satisfied + "</div>");
        xml.append("<div class='completion " + completed + "'>" + completed + "</div>");
        xml.append("<div class='attempts'>" + attempts + "</div>");
        xml.append("<div class='suspended'>" + suspended + "</div>");
        //get total time
        xml.append("<div class='div_detail_arrows' onclick='$(this).parent().find(\"div.activityData\").toggle().parent().toggleClass(\"expandedActivity\");");
        xml.append("$(\"img\",this).attr(\"src\", $(this).parent().find(\"div.activityData\").is(\":hidden\") ? \"images/down-arrow.gif\" : \"images/up-arrow.gif\");'>");
        xml.append("<img class='img_detail_arrows' src='images/down-arrow.gif' /></div>");

        xml.append("<div class='activityData'>");
        xml.append("<table class='table_details'><tr><td class='td_objectives'>");
        
        Element objectives = getFirstChildByTagName(activity, "objectives");
        if(objectives != null){
            addObjectives(objectives);
        }
        
        xml.append("</td><td class='td_runtime'>");
        
        Element runTime = getFirstChildByTagName(activity, "runtime");
        if(runTime != null){
            addRuntime(runTime, actNum);
        }
        
        xml.append("</td></tr></table></div>");
        
        xml.append("</div>");

        Element activityChildren = getFirstChildByTagName(activity, "children");
        List<Element> childActivities = getChildrenByTagName(activityChildren, "activity");
        for(int i = 0; i < childActivities.size(); i++){
            addActivity(childActivities.get(i), actNum+i, leftMargin+15);
        }
    }
    
    public void addObjectives(Element objectivesRoot){
        xml.append("<div class='actObjectiveData'>");
        xml.append("<div class='detailsTopLabel'>Activity Objectives</div>");
        xml.append("<table class='table_details'>");
        List<Element> objectives = getChildrenByTagName(objectivesRoot, "objective");
        for (Element objective : objectives){
                String id = objective.getAttribute("id");
                String measureStatus = getChildElemText(objective, "measurestatus");
                String normMeasure = getChildElemText(objective, "normalizedmeasure");
                String progressStatus = getChildElemText(objective, "progressstatus");
                String satisfiedStatus = getChildElemText(objective, "satisfiedstatus");

                xml.append("<tr><td><span class='actDetailsPropLbl'>Objective Id: </span></td><td><span class='actDetailsPropVal'>" + id + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Measure Status: </span></td><td><span class='actDetailsPropVal'>" + measureStatus + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Normalized Measure: </span></td><td><span class='actDetailsPropVal'>" + normMeasure + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Progress Status: </span></td><td><span class='actDetailsPropVal'>" + progressStatus + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Satisfied Status: </span></td><td><span class='actDetailsPropVal'>" + satisfiedStatus + "</span></td></tr>");
                xml.append("<tr class='tr_space'><td></td><td></td></tr>");

        }
        xml.append("</table>");
        xml.append("</div>");
    }
    
    public void addRuntime(Element runTime, int actNum){
        xml.append("<div class='actRuntimeData'>");
        
        xml.append("<table class='table_details'><tr>");
        
        Element objectivesRoot = getFirstChildByTagName(runTime, "objectives");
        List<Element> objectives = getChildrenByTagName(objectivesRoot, "objective");
        if (objectives.size() > 0){
            xml.append("<td class='td_runtimeObjectives'>");
            xml.append("<div class='detailsTopLabel'>Runtime Objectives</div>");
            xml.append("<table class='table_details'>");
            for (Element obj : objectives) {
                xml.append("<tr><td><span class='actDetailsPropLbl'>Objective Id:</span></td><td><span class='actDetailsPropVal'>" + obj.getAttribute("id") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Scaled Score:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(obj, "score_scaled") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Minimum Score:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(obj, "score_min") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Raw Score:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(obj, "score_raw") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Maximum Score:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(obj, "score_max") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Success Status:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(obj, "success_status") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Completion Status:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(obj, "completion_status") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Progress Measure:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(obj, "progress_measure") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Description:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(obj, "description") + "</span></td></tr>");
                xml.append("<tr class='tr_space'><td></td><td></td></tr>"); 
            }
            xml.append("</table><br/>");
            xml.append("</td>");
            
        }
        
        
        xml.append("<td class='td_runtimeDetails'>");
        
        xml.append("<div class='detailsTopLabel'>Activity Runtime Data</div>");
        
        xml.append("<table class='table_details'>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Completion Status: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "completion_status") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Credit: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "credit") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Entry: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "entry") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Exit: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "exit") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Learner Preferences: </span></td><td></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Audio Level: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "audio_level") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Language: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "language") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Delivery Speed: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "delivery_speed") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Audio Captioning: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "audio_captioning") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Location: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "location") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Mode: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "mode") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Progress Measure: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "progress_measure") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Score Scaled: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "score_scaled") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Score Raw: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "score_raw") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Score Minimum: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "score_min") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Score Maximum: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "score_max") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Total Time: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "total_time") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Time Tracked: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "timetracked") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Success Status: </span></td><td><span class='actDetailsPropVal'>" + getChildElemText(runTime, "success_status") + "</span></td></tr>");
        xml.append("</table>");
        
        //xml.append("</td>");
        
        xml.append("<br />");
        
        //xml.append("<td>");
        
        //xml.append("<div class='detailsTopLabel'>Suspend Data</div>");
        //xml.append("<div class='actDetailsProp'><span class='actDetailsPropVal'>" + getChildElemText(runTime, "suspend_data") + "</span></div>");
        //xml.append("<br/>");
        
        Element staticData = getFirstChildByTagName(runTime, "static");
        xml.append("<div class='detailsTopLabel'>Static Runtime Data</div>");
        xml.append("<table class='table_details'>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Completion Threshold:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(staticData, "completion_threshold") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Launch Data:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(staticData, "launch_data") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Learner Id:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(staticData, "learner_id") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Learner Name:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(staticData, "learner_name") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Maximum Time Allowed:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(staticData, "max_time_allowed") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Scaled Passing Score:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(staticData, "scaled_passing_score") + "</span></td></tr>");
        xml.append("<tr><td><span class='actDetailsPropLbl'>Time Limit Action:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(staticData, "time_limit_action") + "</span></td></tr>");
        xml.append("</table><br/>");
        
        Element interactionsRoot = getFirstChildByTagName(runTime, "interactions");
        List<Element> interactions = getChildrenByTagName(interactionsRoot, "interaction");
        if (interactions.size() > 0){
            xml.append("<div class='detailsTopLabel'>Interactions<div id='interactionArrowDiv' class='sub_detail_arrows' ");
            xml.append("onclick=\'$(\"#interactionsTable" + actNum + "\").toggle(); $(\"img\",this).css(\"right\",$(\"#interactionsTable" + actNum + "\").is(\":hidden\") ? \"images/down-arrow.gif\" : \"images/up-arrow.gif\"); \' >");
            xml.append("<img id='interaction_arrows' class='img_detail_arrows' src='images/down-arrow.gif' />");
            xml.append("</div></div>");
            xml.append("<table id='interactionsTable" + actNum + "' class='interactionsTable table_details'>");

            for (Element interaction : interactions){
                xml.append("<tr><td class='intLblWidth'><span class='actDetailsPropLbl'>Interaction Id:</span></td><td><span class='actDetailsPropVal'>" + interaction.getAttribute("id") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Type:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(interaction, "type") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Timestamp:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(interaction, "timestamp") + "</span></td></tr>");
                xml.append("<tr><td colspan='2'><span class='actDetailsPropLbl margin5'>Objectives:</span></td><td></td></tr>");
                
                Element intObjectivesRoot = getFirstChildByTagName(interaction, "objectives");
                List<Element> intObjectives = getChildrenByTagName(intObjectivesRoot, "objective");
                for (Element intObj : intObjectives){
                    xml.append("<tr><td><span class='actDetailsPropLbl margin20'>Objective Id:</span></td><td><span class='actDetailsPropVal'>" + intObj.getAttribute("id") + "</span></td></tr>");
                }
                
                xml.append("<tr><td colspan='2'><span class='actDetailsPropLbl margin5'>Correct Responses:</span></td></tr>");
                
                Element intResponsesRoot = getFirstChildByTagName(interaction, "correct_responses");
                List<Element> intResponses = getChildrenByTagName(intResponsesRoot, "response");
                for (Element intResp : intResponses){
                    xml.append("<tr><td><span class='actDetailsPropLbl margin20'>Response Id:</span></td><td><span class='actDetailsPropVal'>" + intResp.getAttribute("id") + "</span></td></tr>");
                }
                
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Weighting:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(interaction, "weighting") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Learner Response:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(interaction, "learner_response") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Result:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(interaction, "result") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Latency:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(interaction, "latency") + "</span></td></tr>");
                xml.append("<tr><td><span class='actDetailsPropLbl margin5'>Description:</span></td><td><span class='actDetailsPropVal'>" + getChildElemText(interaction, "description") + "</span></td></tr>");
                xml.append("<tr class='tr_space'><td colspan='2'></td></tr>");
                xml.append("<tr><td class='dotted' colspan='2'></td></tr>");
                xml.append("<tr class='tr_space'><td colspan='2'></td></tr>");
            }
            xml.append("</table><br/>");
        }
        
        //xml.append("<br/>");
        Element learnerCommentsRoot = getFirstChildByTagName(runTime, "comments_from_learner");
        List<Element> learnerComments = getChildrenByTagName(learnerCommentsRoot, "comment");
        if (learnerComments.size() > 0){
            xml.append("<div class='detailsTopLabel'>Comments From Learner<div id='learnerCommentArrowDiv' class='sub_detail_arrows comment_arrow' ");
            xml.append("onclick=\'$(\"#learnerComments'" + actNum + "'\").toggle(); $(\"img\",this).css(\"right\",$(\"#learnerComments'" + actNum + "'\").is(\":hidden\") ? \"images/down-arrow.gif\" : \"images/up-arrow.gif\");\' >");
            
            xml.append("<img id='learnerCommentArrows' class='img_detail_arrows' src='images/down-arrow.gif' />");
            xml.append("</div></div>");
            xml.append("<div id='learnerComments" + actNum + "' class='learnerComments'>");
            for (Element comment : learnerComments){
                xml.append("<div class='commentDetail'><span class='actDetailsPropLbl bold'>Date: </span><span class='actDetailsPropVal'>" + getChildElemText(comment, "date_time") + "</span></div>");
                xml.append("<div class='commentDetail'><span class='actDetailsPropLbl bold'>Location: </span><span class='actDetailsPropVal'>" + getChildElemText(comment, "location") + "</span></div>");
                xml.append("<div class='commentDetail'><span class='actDetailsPropLbl bold'>Comment: </span><span class='actDetailsPropVal'>" + getChildElemText(comment, "value") + "</span></div>");
                xml.append("<br/>");
            }
            xml.append("</div><br/>");
        }
        
        //xml.append("<br/>");
        Element lmsCommentsRoot = getFirstChildByTagName(runTime, "comments_from_lms");
        List<Element> lmsComments = getChildrenByTagName(lmsCommentsRoot, "comment");
        if (lmsComments.size() > 0){
            xml.append("<div class='detailsTopLabel'>Comments From LMS<div id='learnerCommentArrowDiv' class='sub_detail_arrows comment_arrow' ");
            xml.append("onclick=\'$(\"#lmsComments'" + actNum + "'\").toggle(); $(\"img\",this).css(\"right\",$(\"#lmsComments'" + actNum + "'\").is(\":hidden\") ? \"images/down-arrow.gif\" : \"images/up-arrow.gif\");\' >");
            xml.append("<img id='lmsCommentArrows' class='img_detail_arrows' src='images/down-arrow.gif' />");
            xml.append("</div></div>");
            xml.append("<div id='lmsComments" + actNum + "' class='lmsComments'>");
            for (Element comment : lmsComments){ 
                xml.append("<div class='commentDetail'><span class='actDetailsPropLbl bold'>Date: </span><span class='actDetailsPropVal'>" + getChildElemText(comment, "date_time") + "</span></div>");
                xml.append("<div class='commentDetail'><span class='actDetailsPropLbl bold'>Location: </span><span class='actDetailsPropVal'>" + getChildElemText(comment, "location") + "</span></div>");
                xml.append("<div class='commentDetail'><span class='actDetailsPropLbl bold'>Comment: </span><span class='actDetailsPropVal'>" + getChildElemText(comment, "value") + "</span></div>");
                xml.append("<br/>");
            }
            xml.append("</div><br/>");
        }
        
        xml.append("</td>");
        xml.append("</tr></table>");
      
        
        xml.append("</div>");
    }
    
    private String getSatisfiedValue(String satisfiedStatus, String satisfiedVal){
        if("true".equals(satisfiedStatus)){
            if("true".equals(satisfiedVal)){
                return "passed";
            } else {
                return "failed";
            }
        } else {
            return "unknown";
        }
    }
    
    private String getCompletionValue(String completionStatus, String completionVal){
        if("true".equals(completionStatus)){
            if("true".equals(completionVal)){
                return "completed";
            } else {
                return "incomplete";
            }
        } else {
            return "unknown";
        }
    }
    
    /// <summary>
    /// Utility function to retrieve inner text of first elem with tag elementName, or null if not found
    /// </summary>
    /// <param name="parent"></param>
    /// <param name="elementName"></param>
    /// <returns></returns>
    private String getChildElemText(Element parent, String tagName)
    {
        String val = null;
        Element childElem = getFirstChildByTagName(parent, tagName);
        if(childElem != null){
            val = childElem.getTextContent();
        }
        return val;
    }
    
    private Element getFirstChildByTagName(Node parent, String tagName){
        List<Element> children = getChildrenByTagName(parent, tagName);
        return (children.size() == 0) ? null : children.get(0);
    }
    
    private List<Element> getChildrenByTagName(Node parent, String tagName){
        ArrayList<Element> elements = new ArrayList<Element>();
        NodeList children = parent.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);
            if(child instanceof Element){
                Element elem = (Element)children.item(i);
                if(tagName.equals(elem.getTagName())){
                    elements.add(elem);
                }
            }
        }
        return elements;
    }
}
