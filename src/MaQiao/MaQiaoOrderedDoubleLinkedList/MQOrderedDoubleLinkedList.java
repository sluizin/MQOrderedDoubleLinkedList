package MaQiao.MaQiaoOrderedDoubleLinkedList;

import MaQiao.Constants.Constants;
import MaQiao.MaQiaoOrderedDoubleLinkedList.Consts;
import MaQiao.MaQiaoOrderedDoubleLinkedList.Consts.booleanType;

/**
 * 有序两端双向链表
 * @author Sunjian
 */
public final class MQOrderedDoubleLinkedList {
	/**
	 * 链表头部开始
	 */
	private transient Entry entryStart = null;
	/**
	 * 链表尾部
	 */
	private transient Entry entryEnd = null;
	/**
	 * 链表长度
	 */
	private transient int entryCount = 0;
	/**
	 * 链表缓存(把链表截多少段)
	 */
	private transient final Entry[] cacheEntry = new Entry[Consts.cacheLen - 1];

	/**
	 * 转序
	 * @return boolean
	 */
	public final boolean Ordering() {
		if (entryStart == null || entryEnd == null || entryStart == entryEnd) return false;
		locked();
		try {
			Entry f = null;
			for (Entry p = entryStart; p != null; p = p.forward) {
				f = p.next;
				p.next = p.forward;
				p.forward = f;
			}
			f = entryStart;
			entryStart = entryEnd;
			entryEnd = f;
			f = null;
			return true;
		} finally {
			unLocked();
		}

	}

	public boolean put(final IoDLL value) {
		if (value == null) return false;
		locked();
		try {
			Entry f = null;
			Entry EntryDown = getEntryDown(entryStart, entryEnd, value);
			if ((f = searchEntry(value, EntryDown)) != null) {
				f.value = value;
				System.out.println("insert into......");
				return true;
			}
			/* 未找到则 添加链表*/
			addEntry(EntryDown, value);
			//showHeadEnd();
			toString();
			System.out.println("/////////////////////////////////////////////////////////////////////////////////////");
			EntryDown = null;
			return true;
		} finally {
			unLocked();
		}
	}

	/**
	 * 判断接口是否存在，以标识数为标准
	 * @param value IoDLL
	 * @param eStart Entry
	 * @param eEnd Entry
	 * @return boolean
	 */
	public boolean contains(final IoDLL value) {
		locked();
		try {
			return (searchEntry(value, entryStart, entryEnd) != null);
		} finally {
			unLocked();
		}
	}

	/**
	 * 判断接口是否存在，以标识数为标准
	 * @param value IoDLL
	 * @param entryStart Entry
	 * @param entryEnd Entry
	 * @return boolean
	 */
	static final boolean contains(final IoDLL value, final Entry entryStart, final Entry entryEnd) {
		return (searchEntry(value, entryStart, entryEnd) != null);
	}

	/**
	 * 按对象查找位置<br/>
	 * @param value IoDLL
	 * @param entryStart Entry
	 * @param entryEnd Entry
	 * @return Entry
	 */
	private static final Entry searchEntry(final IoDLL value, final Entry entryStart, final Entry entryEnd) {
		for (Entry p = entryStart; p != null && p.value != null && p.value.identityCode() <= value.identityCode(); p = p.next) {
			if (p.value != null && (p.value == value || p.value.equals(value))) return p;
			if (p == entryEnd) break;
		}
		return null;
	}

	private static final Entry searchEntry(final IoDLL value, final Entry entryStart) {
		for (Entry p = entryStart; p != null && p.value != null && p.value.identityCode() <= value.identityCode(); p = p.forward) {
			if (p.value != null && (p.value == value || p.value.equals(value))) return p;
		}
		return null;
	}

	/**
	 * 按对象查找位置<br/>
	 * 允许倒序查找<br/>
	 * @param value IoDLL
	 * @param entryStart Entry
	 * @param entryEnd Entry
	 * @return Entry
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static final Entry searchEntryDeprecated(final IoDLL value, final Entry entryStart, final Entry entryEnd) {
		boolean ordering = true;
		if (entryStart == null) return null;
		if (entryStart.value.identityCode() > (entryEnd == null ? value.identityCode() : entryEnd.value.identityCode())) ordering = false;
		for (Entry p = ordering ? entryStart : entryEnd; p != null && p.value != null && p.value.identityCode() <= value.identityCode(); p = ordering ? p.next : p.forward) {
			if (p.value != null && (p.value == value || p.value.equals(value))) return p;
			if (p == (ordering ? entryEnd : entryStart)) break;
		}
		return null;
	}

	/**
	 * 添加链表，有顺序
	 * @param EntryDown
	 * @param value
	 */
	private final void addEntry(final Entry EntryDown, final IoDLL value) {
		final Entry e = new Entry(value);
		/*空链表时*/
		if (entryStart == null) {
			entryStart = entryEnd = e;
			entryCount++;
			return;
		}
		/* 如果下限为空，则认为全部链表的所有值都小于等于此标记值，直接放在队尾 */
		if (EntryDown == null) {
			e.forward = entryEnd;
			e.next = null;
			entryEnd.next = e;
			entryEnd = e;

		} else {
			/* 如果有下限值，则放入下限对象的前面(默认为从小到大的排序)*/
			if (entryStart == EntryDown) entryStart = e;
			e.next = EntryDown;
			e.forward = EntryDown.forward;
			if (EntryDown.forward != null) EntryDown.forward.next = e;
			EntryDown.forward = e;
		}
		entryCount++;
	}

	/**
	 * 链表缓存初始化
	 */
	private final void initCache() {
		if (entryCount > Consts.cacheCount) {
			final int maxSize = entryCount / (Consts.cacheLen - 1);
			if (maxSize < 1) new StringIndexOutOfBoundsException("Error:initCache()->maxSize change Consts.cacheLen !!!");
			Entry p = entryStart;
			for (int cacheSuffix = 0, Suffix = 0; p != null && p != entryEnd; p = p.next)
				if (++Suffix % maxSize == 0) cacheEntry[cacheSuffix++] = p;
		}
	}

	/**
	 * 在指定锁表中间段，找到某个标记的下限(包含本标记)<br/>
	 * 允许倒序查找<br/>
	 * @param entryStart Entry
	 * @param entryEnd Entry
	 * @param value IoDLL
	 * @return Entry
	 */
	private static final Entry getEntryDown(final Entry entryStart, final Entry entryEnd, final IoDLL value) {
		if (value == null) new StringIndexOutOfBoundsException("IoDLL is not null!!!");
		if (entryStart == null) return null;
		if (entryStart == entryEnd) return entryStart.value.identityCode() > value.identityCode() ? entryStart : null;
		for (Entry p = entryStart; p != null; p = p.next) {
			/* 记录  第一个大于这个值的位置 */
			if (p.value.identityCode() >= value.identityCode()) return p;
			if (p == entryEnd) break;
		}
		return null;
	}

	public final int isOrdering() {
		return isOrdering(entryStart, entryEnd);
	}

	/**
	 * 判断链表段的顺序<br/>
	 * 1:从小到大<br/>
	 * 0:从大到小<br/>
	 * -1:乱序<br/>
	 * @param start Entry
	 * @param end Entry
	 * @return int
	 */
	static final int isOrdering(final Entry start, final Entry end) {
		boolean boolPold = false;//boolP = false, 
		boolean isFirst = true;
		long pIdentitycode, compIdentitycode;
		for (Entry p = start, comp = (p != null) ? p.next : null; p != null && comp != null; comp = ((p = p.next) == null) ? null : p.next) {
			if (p == end) break;
			if ((pIdentitycode = p.value.identityCode()) == (compIdentitycode = comp.value.identityCode())) continue;
			//boolP = pIdentitycode < compIdentitycode;
			if (!isFirst || (isFirst = (!isFirst))) if (boolPold ^ pIdentitycode < compIdentitycode) {
				return -1;
			} else {
				continue;
			}
			boolPold = pIdentitycode < compIdentitycode;
		}
		if (boolPold) return 1;
		return 0;
	}

	@Override
	public String toString() {
		initCache();
		System.out.println("====================================");
		System.out.println("entryCount:" + entryCount);
		int ii = 1;
		for (Entry p = entryStart; p != null; p = p.next)
			entryView(ii++, p);
		System.out.println();
		System.out.println("orderIng:" + isOrdering());
		System.out.println("====================================");
		//		System.out.println("cacheEntry:" + cacheEntry.length);
		//		Entry p = null;
		//		for (int i = 0; i < cacheEntry.length; i++) {
		//			if ((p = cacheEntry[i]) != null) System.out.println("cacheEntry[" + i + "](" + p.value.identityCode() + ")");
		//		}
		//		System.out.println("====================================");
		return "";
	}

	public void showHeadEnd() {
		System.out.println("----------");
		System.out.print("entryStart:");
		entryView(entryStart);
		System.out.println();
		System.out.print("entryEnd:");
		entryView(entryEnd);
		System.out.println();
		System.out.println("----------");
	}

	public void entryView(final Entry p) {
		if (p == null) return;
		System.out.print("[" + p.value.identityCode());
		System.out.print("]{" + p.value.identityName() + "} ");
	}

	public void entryView(final int i, final Entry p) {
		if (p == null) return;
		System.out.print("(" + i + ")[" + p.value.identityCode());
		System.out.print("]{" + p.value.identityName() + "} ");
	}

	/**
	 * 对象单元
	 * @author Sunjian
	 */
	static final class Entry {
		Entry forward;
		IoDLL value;
		Entry next;

		/**
		 * Creates new entry.
		 */
		Entry(IoDLL v) {
			forward = null;
			value = v;
			next = null;
		}
	}

	/**
	 * 锁定对象锁
	 */
	private final void locked() {
		lockedIsReadOffsetCAS(booleanType.False, booleanType.True);
	}

	/**
	 * 释放对象锁
	 */
	private final void unLocked() {
		lockedIsReadOffsetCAS(booleanType.True, booleanType.False);
	}

	/**
	 * volatile int lockedIsRead 主锁的CAS，循环设置锁<br/>
	 * @param from booleanType
	 * @param to booleanType
	 */
	private final void lockedIsReadOffsetCAS(final booleanType from, final booleanType to) {
		while (!Constants.UNSAFE.compareAndSwapInt(this, Consts.OffsetLocked, from.value, to.value)) {
		}
	}

	/** 判断是否进入锁状态 */
	@SuppressWarnings("unused")
	private transient volatile int locked = booleanType.False.value;
}
