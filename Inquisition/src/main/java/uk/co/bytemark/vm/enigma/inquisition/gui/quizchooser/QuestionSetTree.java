package uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser;

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import uk.co.bytemark.vm.enigma.inquisition.misc.ToStringWrapper;
import uk.co.bytemark.vm.enigma.inquisition.questions.QuestionSet;

public class QuestionSetTree extends JTree {

    private static final String    ROOT_NODE_NAME        = "Root";

    private static final String    DEFAULT_CATEGORY_NAME = "Other";

    private DefaultMutableTreeNode rootNode;

    private DefaultTreeModel       defaultTreeModel;

    private DefaultMutableTreeNode defaultNode           = null;

    public QuestionSetTree() {
        // setCellRenderer(new NonLeafItalicTreeCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setRootVisible(false);
        setNewDefaultTreeModel();
    }

    public void initialise(Collection<QuestionSet> bundledQuestionSets) {
        setNewDefaultTreeModel();
        for (QuestionSet questionSet : bundledQuestionSets)
            addQuestionSetToTree(questionSet);
        defaultTreeModel.reload();
        for (int i = 0; i < getRowCount(); i++)
            expandRow(i);
    }

    private void setNewDefaultTreeModel() {
        rootNode = new DefaultMutableTreeNode(ROOT_NODE_NAME);
        defaultTreeModel = new DefaultTreeModel(rootNode);
        setModel(defaultTreeModel);
    }

    public TreePath addQuestionSetToTree(QuestionSet questionSet) {
        List<String> categorySequenceList = questionSet.getCategoryList();
        if (categorySequenceList.size() == 0)
            return addQuestionSetToDefaultCategory(questionSet);
        else
            return addQuestionSetToCategoryHierarchy(questionSet, categorySequenceList);
    }

    public boolean isAQuestionSetSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
        return (node != null && node.isLeaf());
    }

    @SuppressWarnings("unchecked")
    public QuestionSet getSelectedQuestionSet() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
        if (node == null || !node.isLeaf())
            return null;
        ToStringWrapper<QuestionSet> toStringWrapper = (ToStringWrapper<QuestionSet>) node.getUserObject();
        return toStringWrapper.getWrappedObject();
    }

    private TreePath addQuestionSetToCategoryHierarchy(QuestionSet questionSet, List<String> categorySequenceList) {
        TreePath path = new TreePath(rootNode);

        DefaultMutableTreeNode previousNode = rootNode;
        for (String categoryString : categorySequenceList) {
            DefaultMutableTreeNode matchingChildNode = searchChildNodes(previousNode, categoryString);
            if (matchingChildNode != null) {
                previousNode = matchingChildNode;
            } else {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(categoryString);
                addChildToTreeNode(previousNode, newNode);
                previousNode = newNode;
            }
            path = path.pathByAddingChild(previousNode);
        }
        DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(new ToStringWrapper<QuestionSet>(questionSet,
                questionSet.getName()));
        addChildToTreeNode(previousNode, leafNode);
        path = path.pathByAddingChild(leafNode);
        return path;
    }

    private TreePath addQuestionSetToDefaultCategory(QuestionSet questionSet) {
        if (defaultNode == null) {
            defaultNode = new DefaultMutableTreeNode(DEFAULT_CATEGORY_NAME);
            addChildToTreeNode(rootNode, defaultNode);
        }
        DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(new ToStringWrapper<QuestionSet>(questionSet,
                questionSet.getName()));
        addChildToTreeNode(defaultNode, leafNode);
        TreePath path = new TreePath(new Object[] { rootNode, defaultNode, leafNode });
        return path;
    }

    private void addChildToTreeNode(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        int position = -1;
        for (int pos = 0; pos < parent.getChildCount(); pos++) {
            DefaultMutableTreeNode otherChild = (DefaultMutableTreeNode) parent.getChildAt(pos);
            if (otherChild.toString().compareTo(child.toString()) > 0) {
                position = pos;
                break;
            }
        }
        if (position >= 0)
            parent.insert(child, position);
        else
            parent.add(child);
        int[] indexes = new int[] { defaultTreeModel.getIndexOfChild(parent, child) };
        defaultTreeModel.nodesWereInserted(parent, indexes);
    }

    private DefaultMutableTreeNode searchChildNodes(DefaultMutableTreeNode node, String name) {
        Enumeration<?> enumeration = node.children();
        DefaultMutableTreeNode matchingChild = null;
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) enumeration.nextElement();
            Object userObject = childNode.getUserObject();
            if (userObject instanceof String) {
                String nodeName = (String) userObject;
                if (nodeName.equals(name)) {
                    matchingChild = childNode;
                    break;
                }
            }
        }
        return matchingChild;
    }
    //
    // private final static class NonLeafItalicTreeCellRenderer extends DefaultTreeCellRenderer {
    // @Override
    // public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
    // boolean leaf, int row, boolean myHasFocus) {
    // JLabel renderedLabel = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
    // myHasFocus);
    // Font font;
    // if (leaf)
    // font = renderedLabel.getFont().deriveFont(Font.PLAIN);
    // else
    // font = renderedLabel.getFont().deriveFont(Font.ITALIC);
    // renderedLabel.setFont(font);
    // return renderedLabel;
    // }
    // }

}
