package uk.nhs.digital.arc.plugin.util;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class DoctypeDetector {
    public static boolean isContentPublication(Node node) throws RepositoryException {
        boolean isContentPublication = false;

        //* Check to see if the path refers to :-
        // - a document of type Publication
        // - a document called content
        String[] folderPathAndNames = splitToFolderPathAndName(node.getPath());

        if (folderPathAndNames.length == 2) {
            isContentPublication = folderPathAndNames[1].equals("content");
            isContentPublication = isContentPublication && checkFacetType(node,"publication");
        }
        return isContentPublication;
    }

    private static boolean checkFacetType(Node node, String checkAgainst) {
        try {
            if (node.getNodes() != null && node.getNodes().hasNext()) {
                return checkAgainst.equals(node.getNodes().nextNode().getProperty("common:FacetType").getString());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
