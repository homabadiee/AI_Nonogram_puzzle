import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class Nonogram
{

    private State state;
    private int n;
    int[] rowCheck;
    ArrayList<ArrayList<Integer>> row_constraints;
    ArrayList<ArrayList<Integer>> col_constraints;

    public Nonogram(State state,
                    ArrayList<ArrayList<Integer>> row_constraints,
                    ArrayList<ArrayList<Integer>> col_constraints)
    {
        this.state = state;
        this.n = state.getN();
        rowCheck = new int[n];
        this.row_constraints = row_constraints;
        this.col_constraints = col_constraints;
    }


    public void start()
    {
        long tStart = System.nanoTime();
        backtrack(state);
        long tEnd = System.nanoTime();
        System.out.println("Total time: " + (tEnd - tStart) / 1000000000.000000000);
    }

    /* TODO
    *  To use AC_3 algorithm uncomment line "newState.setDomain(AC_3(newState,mrvRes[0], mrvRes[1]));"
    *  in backtrack
    *  unless use forwardChecking with "newState.setDomain(forwardChecking(newState));"
    */
    private boolean backtrack(State state)
    {
        if (isFinished(state))
        {
            System.out.println("Result Board: \n");
            state.printBoard();
            return true;
        }
        if (allAssigned(state))
        {
            return false;
        }

        int[] mrvRes = MRV(state);
        for (String s : LCV(state, mrvRes))
        {
            State newState = state.copy();
            newState.setIndexBoard(mrvRes[0], mrvRes[1], s);
            newState.removeIndexDomain(mrvRes[0], mrvRes[1], s);
//            newState.setDomain(AC_3(newState,mrvRes[0], mrvRes[1]));
            newState.setDomain(forwardChecking(newState));
            if (!isConsistent(newState))
            {
                continue;
            }
            if (backtrack(newState))
            {
                return true;
            }
        }
        return false;
    }


    private ArrayList<ArrayList<ArrayList<String>>> AC_3(State state , int x , int y )
    {
        //update given domain twice with both row and col
        int [] removeResult = reviseRow(state,x);

        if(removeResult[0] == 0)//indicate if removing occur
        {
            state.removeIndexDomain(x,removeResult[1],state.getBoard().get(x).get(removeResult[1]));
        }

        removeResult = reviseCol(state,y);
        if(removeResult[0] == 0)
        {
            state.removeIndexDomain(removeResult[1],y,state.getBoard().get(removeResult[1]).get(y));
        }
        return state.getDomain();
    }

    private int [] reviseRow (State state , int row )
    {
        int [] removeResult = new int[2];
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        int sum = 0;
        for (int x : row_constraints.get(row))
        {
            sum += x;
        }
        int count_f = 0;
        int count_e = 0;
        int count_x = 0;
        for (int j = 0; j < n; j++)
        {
            if (cBoard.get(row).get(j).equals("F"))
            {
                count_f++;
            }
            else if (cBoard.get(row).get(j).equals("E"))
            {
                count_e++;
            }
            else if (cBoard.get(row).get(j).equals("X"))
            {
                count_x++;
            }
        }

        if (count_x > n - sum)
        {
            removeResult[1] = n - 1;
            return removeResult;
        }
        if (count_f != sum && count_e == 0)
        {
            removeResult[1] = n - 1;
            return removeResult;
        }

        Queue<Integer> constraints = new LinkedList<>();
        constraints.addAll(row_constraints.get(row));
        int count = 0;
        boolean flag = false;
        for (int j = 0; j < n; j++)
        {
            if (cBoard.get(row).get(j).equals("F"))
            {
                flag = true;
                removeResult[0] = 1;
                count++;
                if (!constraints.isEmpty() && count > constraints.peek())
                {
                    flag = false;
                    removeResult[0] = 0;
                    removeResult[1] = j;
                    return removeResult;
                }
            }
            else if (cBoard.get(row).get(j).equals("E"))
            {
                break;
            }
            else if (cBoard.get(row).get(j).equals("X"))
            {
                if (flag)
                {
                    flag = false;
                    removeResult[0] = 0;
                    removeResult[1] = j;
                    if (!constraints.isEmpty())
                    {
                        if (count != constraints.peek())
                        {
                            return removeResult;
                        }
                        constraints.remove();
                    }
                    count = 0;
                }
            }
        }
        removeResult[0] = 1;
        return removeResult;
    }


    private int [] reviseCol(State state , int col )
    {
        int [] removeResult = new int[2];
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        int sum = 0;
        for (int x : col_constraints.get(col))
        {
            sum += x;
        }
        int count_f = 0;
        int count_e = 0;
        int count_x = 0;
        for (int i = 0; i < n; i++)
        {
            if (cBoard.get(i).get(col).equals("F"))
            {
                count_f++;
            }
            else if (cBoard.get(i).get(col).equals("E"))
            {
                count_e++;
            } else if (cBoard.get(i).get(col).equals("X"))
            {
                count_x++;
            }
        }
        if (count_x > n - sum)
        {
            removeResult[1] = n - 1;
            return removeResult;
        }
        if (count_f != sum && count_e == 0)
        {
            removeResult[1] = n - 1;
            return removeResult;
        }

        Queue<Integer> constraints = new LinkedList<>();
        constraints.addAll(col_constraints.get(col));
        int count = 0;
        boolean flag = false;
        for (int i = 0; i < n; i++)
        {
            if (cBoard.get(i).get(col).equals("F"))
            {
                flag = true;
                removeResult[0] = 1;
                count++;
                if (!constraints.isEmpty() && count > constraints.peek())
                {
                    flag = false;
                    removeResult[0] = 0;
                    removeResult[1] = i;
                    return removeResult;
                }
            }
            else if (cBoard.get(i).get(col).equals("E"))
            {
                break;
            }
            else if (cBoard.get(i).get(col).equals("X"))
            {
                if (flag)
                {
                    flag = false;
                    removeResult[0] = 0;
                    removeResult[1] = i;
                    if (!constraints.isEmpty())
                    {
                        if (count != constraints.peek())
                        {
                            return removeResult;
                        }
                        constraints.remove();
                    }
                    count = 0;
                }
            }
        }

        removeResult[0] = 1;
        return removeResult;
    }

    private ArrayList<String> LCV(State state, int[] var)
    {
        int count_F = 0;
        int row_sum = 0;
        for (int i = 0; i < state.getBoard().get(var[0]).size(); i++)
        {
            if (state.getBoard().get(var[0]).get(i).equals("F"))
            {
                count_F++;
            }
            if (i < row_constraints.get(var[0]).size())
            {
                row_sum += row_constraints.get(var[0]).get(i);
            }
        }
        if (row_sum == count_F)
        {
            state.removeIndexDomain(var[0], var[1], "F");
        }
        return state.getDomain().get(var[0]).get(var[1]);
    }

    private int[] MRV(State state)
    {
        ArrayList<ArrayList<String>> board = state.getBoard();
        ArrayList<ArrayList<ArrayList<String>>> domain = state.getDomain();

        int min = 10000;
        int[] result = new int[2];

        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (board.get(i).get(j).equals("E"))
                {
                    int constraintNum = domain.get(i).get(j).size();
                    if (constraintNum < min)
                    {
                        min = constraintNum;
                        result[0] = i;
                        result[1] = j;
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<ArrayList<ArrayList<String>>> forwardChecking(State state)
    {
        int counter_row = 0;
        int sum_row = 0;
        for (int i = 0; i < n; i++)
        {
            if (rowCheck[i] == 1)
            {
                continue;
            }
            for (int j = 0; j < n; j++)
            {
                if (state.getBoard().get(i).get(j).equals("F"))
                {
                    counter_row++;
                }
                if (j < row_constraints.get(i).size())
                {
                    sum_row += row_constraints.get(i).get(j);
                }
            }
            if (sum_row == counter_row && isConsistent(state))
            {
                for (int l = 0; l < n; l++)
                {
                    String stn = state.getBoard().get(i).toString();
                    if (!stn.contains("E"))
                    {
                        continue;
                    }
                    if (state.getBoard().get(i).get(l).equals("E"))
                    {
                        state.setIndexBoard(i, l, "X");
                        state.removeIndexDomain(i, l, "F");
                    }

                }

                rowCheck[i] = 1;
            }
            counter_row = 0;
        }
        return state.getDomain();
    }


    private boolean allAssigned(State state) {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String s = cBoard.get(i).get(j);
                if (s.equals("E"))
                    return false;
            }
        }
        return true;
    }


    private boolean isConsistent(State state)
    {
        ArrayList<ArrayList<String>> cBoard = state.getBoard();
        //check row constraints
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int x : row_constraints.get(i)) {
                sum += x;
            }
            int count_f = 0;
            int count_e = 0;
            int count_x = 0;
            for (int j = 0; j < n; j++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    count_f++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    count_e++;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    count_x++;
                }
            }

            if (count_x > n - sum) {
                return false;
            }
            if (count_f != sum && count_e == 0) {
                return false;
            }

            Queue<Integer> constraints = new LinkedList<>();
            constraints.addAll(row_constraints.get(i));
            int count = 0;
            boolean flag = false;
            for (int j = 0; j < n; j++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    flag = true;
                    count++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    break;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    if (flag) {
                        flag = false;
                        if (!constraints.isEmpty()) {
                            if (count != constraints.peek()) {
                                return false;
                            }
                            constraints.remove();
                        }
                        count = 0;
                    }
                }
            }

        }

        //check col constraints

        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int x : col_constraints.get(j)) {
                sum += x;
            }
            int count_f = 0;
            int count_e = 0;
            int count_x = 0;
            for (int i = 0; i < n; i++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    count_f++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    count_e++;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    count_x++;
                }
            }
            if (count_x > n - sum) {
                return false;
            }
            if (count_f != sum && count_e == 0) {
                return false;
            }

            Queue<Integer> constraints = new LinkedList<>();
            constraints.addAll(col_constraints.get(j));
            int count = 0;
            boolean flag = false;
            for (int i = 0; i < n; i++) {
                if (cBoard.get(i).get(j).equals("F")) {
                    flag = true;
                    count++;
                } else if (cBoard.get(i).get(j).equals("E")) {
                    break;
                } else if (cBoard.get(i).get(j).equals("X")) {
                    if (flag) {
                        flag = false;
                        if (!constraints.isEmpty()) {
                            if (count != constraints.peek()) {
                                return false;
                            }
                            constraints.remove();
                        }
                        count = 0;
                    }
                }
            }
        }
        return true;
    }

    private boolean isFinished(State state) {
        return allAssigned(state) && isConsistent(state);
    }

}
