package uk.nhs.digital.arc.plugin.util;

import org.apache.commons.lang.StringUtils;
import uk.nhs.digital.arc.util.FilePathData;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

public class DoctypeDetector {

    public static final String PUBLICATIONSYSTEM_MANIFEST_LOCATION = "publicationsystem:manifestLocation";

    public static boolean isContentPublication(Node node) throws RepositoryException {
        boolean isContentPublication = false;

        //* Check to see if the path refers to :-
        // - a document of type Publication
        // - a document called content
        String[] folderPathAndNames = splitToFolderPathAndName(node.getPath());

        if (folderPathAndNames.length == 2) {
            isContentPublication = folderPathAndNames[1].equals("content");
            isContentPublication = isContentPublication && checkFacetTypeAndManifestLocation(node,"publication");
        }
        return isContentPublication;
    }

    private static boolean checkFacetTypeAndManifestLocation(Node node, String checkAgainst) {
        try {
            if (node.getNodes() != null && node.getNodes().hasNext()) {
                Node nodeToCheck = node.getNodes().nextNode();

                return checkAgainst.equals(nodeToCheck.getProperty("common:FacetType").getString())
                    && isLocation(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Find the value of the manifestLocation property and do a cursory check on the protocol specified
     * @param node is the node under consideration
     * @return the value of the manifestLocation property if available, else null
     * @throws RepositoryException when an exception occurs doing the lookup
     */
    public static String getManifestLocationValue(Node node) throws RepositoryException {
        Property nodeLoc = node.getNodes().nextNode().getProperty(PUBLICATIONSYSTEM_MANIFEST_LOCATION);

        if (checkNodeLocationAppearsToBeValid(nodeLoc)) {
            return nodeLoc.getString();
        }

        return null;
    }

    /**
     * Check to see if we think this really is a properly qualified URL
     * @param node is the node under consideration
     * @return a boolean to indicate if the value is likely to be a location
     * @throws RepositoryException if we encounter an exception
     */
    private static boolean isLocation(Node node) {
        try {
            Node next = node.getNodes().nextNode();

            if (next != null) {
                Property nodeLoc = null;
                try {
                    nodeLoc = next.getProperty(PUBLICATIONSYSTEM_MANIFEST_LOCATION);
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }

                return checkNodeLocationAppearsToBeValid(nodeLoc);
            }
        } catch (PathNotFoundException pnf) {
            return false;
        } catch (RepositoryException re) {
            re.printStackTrace();
        }

        return false;
    }

    /**
     * This does the actual check on the node property
     * @param nodeLoc is the property associated with the location
     * @return a boolean to indicate whether this is a URL
     * @throws RepositoryException if we encounter an exception
     */
    private static boolean checkNodeLocationAppearsToBeValid(Property nodeLoc) throws RepositoryException {
        if (nodeLoc != null && !StringUtils.isEmpty(nodeLoc.toString())) {
            FilePathData fpu = new FilePathData("", nodeLoc.getString());
            return fpu.looksLikeAUrl();
        }
        return false;
    }

    public static String[] splitToFolderPathAndName(String path) {
        String[] folderPath = new String[2];

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        int index = path.lastIndexOf("/");
        folderPath[0] = path.substring(0, index);
        folderPath[1] = path.substring(index + 1);

        return folderPath;
    }
}
