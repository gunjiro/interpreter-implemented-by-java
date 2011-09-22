interface Value {
    public Value apply(Thunk arg) throws EvaluationException;
    public Value getHead() throws EvaluationException;
    public ListValue getTail() throws EvaluationException;
    public IntValue toIntValue() throws IntOperationException;
    public ListValue toListValue() throws ListOperationException;
}

abstract class ListValue implements Value {
    public static final ListValue EMPTY = new EmptyListValue();
    @Override
    public Value apply(Thunk arg) throws ApplyException {
        throw new ApplyException("���X�g�Ɉ�����^���邱�Ƃ͂ł��܂���B");
    }
    @Override
    public IntValue toIntValue() throws IntOperationException {
        throw new IntOperationException("���X�g�͐����ɕϊ��o���܂���B");
    }
    @Override
    public ListValue toListValue() {
        return this;
    }
    public abstract boolean isEmpty();
    private static class EmptyListValue extends ListValue {
        @Override
        public boolean isEmpty() {
            return true;
        }
        @Override
        public Value getHead() throws ListOperationException {
            throw new ListOperationException("head: �󃊃X�g�ł��B");
        }
        @Override
        public ListValue getTail() throws ListOperationException {
            throw new ListOperationException("tail: �󃊃X�g�ł��B");
        }
    }
}

class HeadTailListValue extends ListValue {
    private final Thunk head;
    private final Thunk tail;
    HeadTailListValue(Thunk h, Thunk t) {
        head = h;
        tail = t;
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
    @Override
    public Value getHead() throws EvaluationException {
        return head.eval();
    }
    @Override
    public ListValue getTail() throws EvaluationException {
        return tail.eval().toListValue();
    }
}

class IntValue implements Value {
    public static final IntValue TRUE = new IntValue(new Integer(1));
    public static final IntValue FALSE = new IntValue(new Integer(0));
    private final int value;
    IntValue(int val) {
        value = val;
    }
    @Override
    public Value apply(Thunk arg) throws ApplyException {
        throw new ApplyException("�����Ɉ�����^���邱�Ƃ͂ł��܂���B");
    }
    public boolean isTrue() {
        return (value != 0) ? true : false;
    }
    @Override
    public Value getHead() throws ListOperationException {
        throw new ListOperationException("��������head����邱�Ƃ͂ł��܂���B");
    }
    @Override
    public ListValue getTail() throws ListOperationException {
        throw new ListOperationException("��������tail����邱�Ƃ͂ł��܂���B");
    }
    @Override
    public IntValue toIntValue() {
        return this;
    }
    @Override
    public ListValue toListValue() throws ListOperationException {
        throw new ListOperationException("�����̓��X�g�ɕϊ��ł��܂���B");
    }
    public int getValue() {
        return value;
    }
}

abstract class CurryFunction implements Value {
    private final int argNum;
    CurryFunction(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        argNum = n;
    }
    @Override
    public Value apply(Thunk t) throws EvaluationException {
        if (argNum > 1) {
            return new AppliedFunction(this, t);
        }
        else {
            ThunkTable table = createArgsTable();
            table.add(t);
            return getExpression().evalWith(table);
        }
    }
    @Override
    public Value getHead() throws ListOperationException {
        throw new ListOperationException("�֐�����head����邱�Ƃ͂ł��܂���B");
    }
    @Override
    public ListValue getTail() throws ListOperationException {
        throw new ListOperationException("�֐�����tail����邱�Ƃ͂ł��܂���B");
    }
    @Override
    public IntValue toIntValue() throws IntOperationException {
        throw new IntOperationException("���������֐��͐����ɕϊ��ł��܂���B");
    }
    @Override
    public ListValue toListValue() throws ListOperationException {
        throw new ListOperationException("���������֐��̓��X�g�ɕϊ��ł��܂���B");
    }
    protected int getArgNum() {
        return argNum;
    }
    protected abstract ThunkTable createArgsTable();
    protected abstract Expression getExpression();
}

class BaseFunction extends CurryFunction {
    private final ThunkTable parentTable;
    private final Expression exp;
    BaseFunction(ThunkTable parent, Expression e, int n) {
        super(n);
        parentTable = parent;
        exp = e;
    }
    @Override
    protected ThunkTable createArgsTable() {
        return new ThunkTable(parentTable, this.getArgNum());
    }
    @Override
    protected Expression getExpression() {
        return exp;
    }
}

class AppliedFunction extends CurryFunction {
    private final CurryFunction func;
    private final Thunk arg;
    AppliedFunction(CurryFunction cf, Thunk t) {
        super(cf.getArgNum() - 1);
        func = cf;
        arg = t;
    }
    @Override
    protected ThunkTable createArgsTable() {
        ThunkTable table = func.createArgsTable();
        table.add(arg);
        return table;
    }
    @Override
    protected Expression getExpression() {
        return func.getExpression();
    }
}
