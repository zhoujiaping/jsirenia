package druid.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsirenia.util.runtime.RuntimeUtil;

import com.alibaba.druid.sql.ast.expr.SQLIntervalExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterCharacter;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareConditionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareHandlerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterEventStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterLogFileGroupStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterServerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTablespaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlChecksumTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateAddLogFileGroupStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateEventStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateServerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableSpaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement.UserSpecification;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlEventSchedule;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExplainStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlFlushStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement.Item;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasePartitionStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowIndexesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowKeysStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByList;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MysqlDeallocatePrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class CryptMysqlAstInsertVisitor extends MySqlASTVisitorAdapter {
	private String table;
	private List<String> columns = new ArrayList<>();
	private List<Boolean> needCrypt = new ArrayList<>();
	private Map<String, Map<String, String>> cryptColumns;

	@Override
	public boolean visit(MySqlTableIndex x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlTableIndex x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlPrimaryKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlPrimaryKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public void endVisit(SQLIntervalExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(SQLIntervalExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlExtractExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlExtractExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlMatchAgainstExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlMatchAgainstExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlPrepareStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlPrepareStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlExecuteStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlExecuteStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MysqlDeallocatePrepareStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MysqlDeallocatePrepareStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlDeleteStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlDeleteStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlInsertStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlInsertStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlLoadDataInFileStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlLoadDataInFileStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlLoadXmlStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlLoadXmlStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowColumnsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowColumnsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowDatabasesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowDatabasesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowWarningsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowWarningsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(CobarShowStatus x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(CobarShowStatus x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlKillStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlKillStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlBinlogStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlBinlogStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlResetStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlResetStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateUserStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateUserStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(UserSpecification x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(UserSpecification x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlPartitionByKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlPartitionByKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public boolean visit(MySqlSelectQueryBlock x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSelectQueryBlock x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlOutFileExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlOutFileExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlExplainStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlExplainStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUpdateStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUpdateStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlSetTransactionStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSetTransactionStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowAuthorsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowAuthorsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowBinaryLogsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowBinaryLogsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowMasterLogsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowMasterLogsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCollationStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCollationStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowBinLogEventsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowBinLogEventsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCharacterSetStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCharacterSetStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowContributorsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowContributorsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateDatabaseStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateDatabaseStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateEventStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateEventStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateFunctionStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateFunctionStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateProcedureStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateProcedureStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateTriggerStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateTriggerStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateViewStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateViewStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowEngineStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowEngineStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowEnginesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowEnginesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowErrorsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowErrorsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowEventsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowEventsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowFunctionCodeStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowFunctionCodeStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowFunctionStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowFunctionStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowGrantsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowGrantsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUserName x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUserName x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowIndexesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowIndexesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowKeysStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowKeysStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowMasterStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowMasterStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowOpenTablesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowOpenTablesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowPluginsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowPluginsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowPrivilegesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowPrivilegesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProcedureCodeStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProcedureCodeStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProcedureStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProcedureStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProcessListStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProcessListStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProfileStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProfileStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProfilesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProfilesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowRelayLogEventsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowRelayLogEventsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowSlaveHostsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowSlaveHostsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowSlaveStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowSlaveStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowTableStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowTableStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowTriggersStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowTriggersStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowVariantsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowVariantsStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlRenameTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlRenameTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUseIndexHint x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUseIndexHint x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlIgnoreIndexHint x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlIgnoreIndexHint x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlLockTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlLockTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(Item x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public boolean visit(com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement.Item x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(Item x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public void endVisit(com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement.Item x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUnlockTablesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUnlockTablesStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlForceIndexHint x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlForceIndexHint x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableChangeColumn x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableChangeColumn x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(SQLAlterCharacter x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(SQLAlterCharacter x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableOption x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableOption x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlHelpStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlHelpStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCharExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCharExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUnique x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUnique x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MysqlForeignKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MysqlForeignKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableModifyColumn x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableModifyColumn x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableDiscardTablespace x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableDiscardTablespace x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableImportTablespace x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableImportTablespace x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(TableSpaceOption x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(TableSpaceOption x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAnalyzeStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAnalyzeStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterUserStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterUserStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlOptimizeStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlOptimizeStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlHintStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlHintStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlOrderingExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlOrderingExpr x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCaseStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCaseStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlDeclareStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlDeclareStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlSelectIntoStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSelectIntoStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlWhenStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlWhenStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlLeaveStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlLeaveStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlIterateStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlIterateStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlRepeatStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlRepeatStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCursorDeclareStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCursorDeclareStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUpdateTableSource x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUpdateTableSource x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableAlterColumn x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableAlterColumn x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlSubPartitionByKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSubPartitionByKey x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlSubPartitionByList x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSubPartitionByList x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlDeclareHandlerStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlDeclareHandlerStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlDeclareConditionStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlDeclareConditionStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlFlushStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlFlushStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlEventSchedule x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlEventSchedule x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateEventStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateEventStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateAddLogFileGroupStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateAddLogFileGroupStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateServerStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateServerStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateTableSpaceStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateTableSpaceStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterEventStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterEventStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterLogFileGroupStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterLogFileGroupStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterServerStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterServerStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTablespaceStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTablespaceStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowDatabasePartitionStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowDatabasePartitionStatusStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlChecksumTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlChecksumTableStatement x) {
		StackTraceElement[] stacks = RuntimeUtil.stackTraceElements();
		StackTraceElement stack = stacks[1];
		System.out.println(stack.getMethodName() + "(" + this.getClass().getName() + ":" + stack.getLineNumber() + ")");
		super.endVisit(x);
	}

}
