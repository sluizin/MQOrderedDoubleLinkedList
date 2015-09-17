package MaQiao.MaQiaoOrderedDoubleLinkedList;

import MaQiao.Constants.Constants;

/**
 * 常量池 <br/>
 * @author Sun.jian(孙.健) <br/>
 */
public final class Consts {
	/**
	 * 初始化
	 */
	public Consts() {
	}

	/**
	 * [阈值]链表缓存(把链表截多少段) > 1
	 */
	static final int cacheLen = 3;
	/**
	 * [阈值]链表缓存长度(每段链表的长度) > 0
	 */
	static final int cacheSize = 3;
	/**
	 * 超过此值，则进入缓存状态
	 */
	static final int cacheCount = cacheLen * cacheSize;
	/**
	 * 本对象的锁对象 locked 的地址偏移量
	 */
	static long OffsetLocked = 0L;
	static {
		try {
			OffsetLocked = Constants.UNSAFE.objectFieldOffset(MQOrderedDoubleLinkedList.class.getDeclaredField("locked"));/*得到锁对象的偏移量*/
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Consts.sequence.Reverse:从大到小<br/>
	 * Consts.sequence.Ordering:从小到大<br/>
	 * Consts.sequence.Outoforder:乱序<br/>
	 * @author Sunjian<br/>
	 */
	static enum sequence {
		/**
		 * 倒序
		 */
		Reverse(1),
		/**
		 * 顺序
		 */
		Ordering(0),
		/**
		 * 乱序
		 */
		Outoforder(-1);
		int value;

		private sequence(final int value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.name() + ":" + this.ordinal();
		}
	}

	/**
	 * boolean的状态:<br/>
	 * False(0):假<br/>
	 * True(1):真<br/>
	 * @author Sunjian
	 */
	static enum booleanType {
		/**
		 * 假(0)
		 */
		False(0),
		/**
		 * 真(1)
		 */
		True(1);
		/**
		 * False:假(0)<br/>
		 * True:真(1)<br/>
		 */
		int value;

		/**
		 * 构造初始化
		 * @param index int
		 */
		private booleanType(final int value) {
			this.value = value;
		}
	}
}
