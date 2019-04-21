package org.jsirenia.dubbodemo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 统一异常处理
 * @author zjp
 *
 */
public class ExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

	public static void handle(Callback cb){
		try {
			cb.apply();
		} catch (ServiceException e) {
			if(e.getCause()!=null){
				logger.error("",e);
			}else{
				if(e.getCode().equals("9999")){
					logger.error("",e);
				}else{
					logger.info(e.getMessage());
				}
			}
		}catch(Exception e){
			logger.error("",e);
		}
	}
	public interface Callback {
		void apply();
	}
}
