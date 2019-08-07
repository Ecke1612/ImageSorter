package logic.filesorter;


public class FileSorterFactory {

    public FileSorterInterface getFileSorter(boolean isTempState, boolean isCut) {
        if(isTempState) {
            System.out.println("return not Move");
            return new FileSorterTemp();
        } else if(!isTempState && !isCut) {
            System.out.println("return move and not Cut");
            return new FileSorterMove();
        } else if(!isTempState && isCut) {
            System.out.println("return move and Cut");
            return new FileSorterMoveAndCut();
        } else {
            System.out.println("FileSorterFactory: not exwpected FileSorter");
            return null;
        }
    }

}
