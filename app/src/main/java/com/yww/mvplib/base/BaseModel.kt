package com.yww.mvplib.base

/**
 * @author  WAVENING
 */
abstract class BaseModel<L : ContractBase.BaseListener>(var listener: L) : ContractBase.BaseModel<L>
