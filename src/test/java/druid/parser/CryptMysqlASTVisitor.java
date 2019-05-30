package druid.parser;

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

/*
 * 我需要获取的信息
 * 参数：设置到【哪个表】的【哪个字段】，或者跟【那个表】的【哪个字段】运算
 * 结果：结果字段【字段名】及最终来自【哪个表】，或者经过运算，那么运算之前，它是来自【哪个表】
 * 收集到这些信息之后，再通过MySqlExportParameterVisitor提取参数，将相关的参数用密文替换。
 * 然后执行sql。
 * 拿到结果之后，再对结果相关字段进行解密。
 * 将解密后的内容作为最终结果。
 */
public class CryptMysqlASTVisitor extends MySqlASTVisitorAdapter{
	
	@Override
	public boolean visit(MySqlTableIndex x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlTableIndex x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlKey x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlKey x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlPrimaryKey x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlPrimaryKey x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public void endVisit(SQLIntervalExpr x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(SQLIntervalExpr x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlExtractExpr x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlExtractExpr x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlMatchAgainstExpr x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlMatchAgainstExpr x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlPrepareStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlPrepareStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlExecuteStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlExecuteStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MysqlDeallocatePrepareStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MysqlDeallocatePrepareStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlDeleteStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlDeleteStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlInsertStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlInsertStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlLoadDataInFileStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlLoadDataInFileStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlLoadXmlStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlLoadXmlStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowColumnsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowColumnsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowDatabasesStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowDatabasesStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowWarningsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowWarningsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowStatusStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowStatusStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(CobarShowStatus x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(CobarShowStatus x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlKillStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlKillStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlBinlogStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlBinlogStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlResetStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlResetStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateUserStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateUserStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(UserSpecification x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(UserSpecification x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlPartitionByKey x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlPartitionByKey x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public boolean visit(MySqlSelectQueryBlock x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSelectQueryBlock x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlOutFileExpr x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlOutFileExpr x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlExplainStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlExplainStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUpdateStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUpdateStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlSetTransactionStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSetTransactionStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowAuthorsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowAuthorsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowBinaryLogsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowBinaryLogsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowMasterLogsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowMasterLogsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCollationStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCollationStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowBinLogEventsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowBinLogEventsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCharacterSetStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCharacterSetStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowContributorsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowContributorsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateDatabaseStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateDatabaseStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateEventStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateEventStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateFunctionStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateFunctionStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateProcedureStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateProcedureStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateTableStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateTableStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateTriggerStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateTriggerStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowCreateViewStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowCreateViewStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowEngineStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowEngineStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowEnginesStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowEnginesStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowErrorsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowErrorsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowEventsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowEventsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowFunctionCodeStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowFunctionCodeStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowFunctionStatusStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowFunctionStatusStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowGrantsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowGrantsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUserName x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUserName x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowIndexesStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowIndexesStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowKeysStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowKeysStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowMasterStatusStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowMasterStatusStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowOpenTablesStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowOpenTablesStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowPluginsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowPluginsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowPrivilegesStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowPrivilegesStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProcedureCodeStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProcedureCodeStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProcedureStatusStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProcedureStatusStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProcessListStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProcessListStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProfileStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProfileStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowProfilesStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowProfilesStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowRelayLogEventsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowRelayLogEventsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowSlaveHostsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowSlaveHostsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowSlaveStatusStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowSlaveStatusStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowTableStatusStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowTableStatusStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowTriggersStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowTriggersStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowVariantsStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowVariantsStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlRenameTableStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlRenameTableStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUseIndexHint x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUseIndexHint x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlIgnoreIndexHint x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlIgnoreIndexHint x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlLockTableStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlLockTableStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(Item x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public boolean visit(MySqlLockTableStatement.Item x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(Item x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public void endVisit(MySqlLockTableStatement.Item x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUnlockTablesStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUnlockTablesStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlForceIndexHint x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlForceIndexHint x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableChangeColumn x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableChangeColumn x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(SQLAlterCharacter x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(SQLAlterCharacter x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableOption x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableOption x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateTableStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateTableStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlHelpStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlHelpStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCharExpr x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCharExpr x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUnique x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUnique x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MysqlForeignKey x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MysqlForeignKey x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableModifyColumn x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableModifyColumn x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableDiscardTablespace x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableDiscardTablespace x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableImportTablespace x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableImportTablespace x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(TableSpaceOption x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(TableSpaceOption x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAnalyzeStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAnalyzeStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterUserStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterUserStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlOptimizeStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlOptimizeStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlHintStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlHintStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlOrderingExpr x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlOrderingExpr x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCaseStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCaseStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlDeclareStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlDeclareStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlSelectIntoStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSelectIntoStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlWhenStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlWhenStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlLeaveStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlLeaveStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlIterateStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlIterateStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlRepeatStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlRepeatStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCursorDeclareStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCursorDeclareStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlUpdateTableSource x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlUpdateTableSource x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTableAlterColumn x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTableAlterColumn x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlSubPartitionByKey x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSubPartitionByKey x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlSubPartitionByList x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlSubPartitionByList x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlDeclareHandlerStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlDeclareHandlerStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlDeclareConditionStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlDeclareConditionStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlFlushStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlFlushStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlEventSchedule x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlEventSchedule x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateEventStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateEventStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateAddLogFileGroupStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateAddLogFileGroupStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateServerStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateServerStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlCreateTableSpaceStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlCreateTableSpaceStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterEventStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterEventStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterLogFileGroupStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterLogFileGroupStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterServerStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterServerStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlAlterTablespaceStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlAlterTablespaceStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlShowDatabasePartitionStatusStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlShowDatabasePartitionStatusStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

	@Override
	public boolean visit(MySqlChecksumTableStatement x) {
		// TODO Auto-generated method stub
		return super.visit(x);
	}

	@Override
	public void endVisit(MySqlChecksumTableStatement x) {
		// TODO Auto-generated method stub
		super.endVisit(x);
	}

}
