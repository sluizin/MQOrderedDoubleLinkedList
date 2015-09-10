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
	private transient Entry[] cacheEntry = new Entry[Consts.cacheLen - 1];

	public MQOrderedDoubleLinkedList() {

	}

	/**
	 * 转序
	 * @return boolean
	 */
	public final boolean orderingTurn() {
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

	/**
	 * 添加单元，如发现同标记则覆盖<br/>
	 * @param value IoDLL
	 * @return boolean
	 */
	public boolean put(final IoDLL value) {
		if (value == null) return false;
		return put(value, true);
	}

	/**
	 * 添加单元，手动决定是否覆盖<br/>
	 * @param value IoDLL
	 * @param isCover boolean
	 * @return boolean
	 */
	public boolean put(final IoDLL value, final boolean isCover) {
		if (value == null) return false;
		locked();
		try {
			Entry f = null;
			Entry EntryDown = EntryGetDown(entryStart, entryEnd, value);
			if ((f = EntrySearch(value, EntryDown)) != null && isCover) {
				f.value = value;
				System.out.println("insert into......");
				return true;
			}
			/* 未找到则 添加链表*/
			privateAddEntry(EntryDown, value);
			//showHeadEnd();
			//toString();
			//System.out.println("/////////////////////////////////////////////////////////////////////////////////////");
			EntryDown = null;
			return true;
		} finally {
			unLocked();
		}
	}

	/**
	 * 添加单元，直接在链表后面加入单元<br/>
	 * @param value IoDLL
	 * @return boolean
	 */
	@Deprecated
	public boolean putFast(final IoDLL value) {
		if (value == null) return false;
		locked();
		try {
			/* 未找到则 添加链表*/
			privateAddEntry(null, value);
			return true;
		} finally {
			unLocked();
		}
	}
	/**
	 * 按标识值得到单元<br/>
	 * @param identityCode
	 * @return
	 */
	public final Entry get(final long identityCode) {
		final boolean order = isOrdering() == 1;
		for (Entry p = (order) ? this.entryEnd : this.entryStart; p != null && p.value.identityCode() <= identityCode; p = (order) ? p.forward : p.next)
			if (p.value.identityCode() == identityCode) return p;
		return null;
	}

	/**
	 * 得到链表头<br/>
	 * @return Entry
	 */
	public final Entry getFirst() {
		return this.entryStart;
	}

	/**
	 * 得到链表尾
	 * @return Entry
	 */
	public final Entry getLast() {
		return this.entryEnd;
	}

	public final void remove(final long identityCode) {
		locked();
		try {
			for (Entry p = entryStart; p != null; p = p.next) {

				if (p == this.entryEnd) break;
			}
		} finally {
			unLocked();
		}

	}

	/**
	 * 删除链表头
	 */
	public final void removeFirst() {
		locked();
		try {
			//Entry p = this.entryStart;
			(this.entryStart = this.entryStart.next).forward = null;
			//p = null;
		} finally {
			unLocked();
		}
	}

	/**
	 * 删除链表尾
	 */
	public final void removeLast() {
		locked();
		try {
			//Entry p = this.entryEnd;
			(this.entryEnd = this.entryEnd.forward).next = null;
			//p = null;
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
			return (EntrySearch(value, entryStart, entryEnd) != null);
		} finally {
			unLocked();
		}
	}

	/**
	 * 把乱序的链表顺序重置，按从小到大重新整理<br/>
	 * @return boolean
	 */
	public final boolean orderingReset() {
		if (entryStart == null) return false;
		locked();
		try {
			final MQOrderedDoubleLinkedList MQdll = new MQOrderedDoubleLinkedList();
			for (Entry p = entryStart; p != null; p = p.next)
				MQdll.put(p.value);
			privateShallowCopy(MQdll, this);
			return true;
		} finally {
			unLocked();
		}
	}

	/**
	 * 判断链表段的顺序<br/>
	 * 1:从大到小<br/>
	 * 0:从小到大<br/>
	 * -1:乱序<br/>
	 * @return int
	 */
	public final int isOrdering() {
		return EntryOrderingIs(entryStart, entryEnd);
	}

	/**
	 * 浅复制链表<br/>
	 * @return MQOrderedDoubleLinkedList
	 */
	@Override
	public final MQOrderedDoubleLinkedList clone() {
		final MQOrderedDoubleLinkedList f = new MQOrderedDoubleLinkedList();
		f.entryStart = this.entryStart;
		f.entryEnd = this.entryEnd;
		f.entryCount = this.entryCount;
		f.cacheEntry = this.cacheEntry;
		return f;
	}

	/*
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				Private 私有方法

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	 */

	/**
	 * 浅复制链表<br/>
	 * @param e MQOrderedDoubleLinkedList
	 * @param f MQOrderedDoubleLinkedList
	 */
	private final void privateShallowCopy(final MQOrderedDoubleLinkedList e, final MQOrderedDoubleLinkedList f) {
		f.entryStart = e.entryStart;
		f.entryEnd = e.entryEnd;
		f.entryCount = e.entryCount;
		f.cacheEntry = e.cacheEntry;
	}

	/**
	 * 链表缓存初始化
	 */
	private final void privateInitCache() {
		if (entryCount > Consts.cacheCount) {
			final int maxSize = entryCount / (Consts.cacheLen - 1);
			if (maxSize < 1) new StringIndexOutOfBoundsException("Error:initCache()->maxSize change Consts.cacheLen !!!");
			Entry p = entryStart;
			for (int cacheSuffix = 0, Suffix = 0; p != null && p != entryEnd; p = p.next)
				if (++Suffix % maxSize == 0) cacheEntry[cacheSuffix++] = p;
		}
	}

	/**
	 * 添加链表，有顺序
	 * @param EntryDown Entry
	 * @param value IoDLL
	 */
	private final void privateAddEntry(final Entry EntryDown, final IoDLL value) {
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

	/*
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				Static静态方法

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	 */

	/**
	 * 判断接口是否存在，以标识数为标准
	 * @param value IoDLL
	 * @param entryStart Entry
	 * @param entryEnd Entry
	 * @return boolean
	 */
	static final boolean EntryContains(final IoDLL value, final Entry entryStart, final Entry entryEnd) {
		return (EntrySearch(value, entryStart, entryEnd) != null);
	}

	/**
	 * 按对象查找位置<br/>
	 * @param value IoDLL
	 * @param start Entry
	 * @param end Entry
	 * @return Entry
	 */
	static final Entry EntrySearch(final IoDLL value, final Entry start, final Entry end) {
		for (Entry p = start; p != null && p.value != null && p.value.identityCode() <= value.identityCode(); p = p.next) {
			if (p.value != null && (p.value == value || p.value.equals(value))) return p;
			if (p == end) break;
		}
		return null;
	}

	static final Entry EntrySearch(final IoDLL value, final Entry entryStart) {
		for (Entry p = entryStart; p != null && p.value != null && p.value.identityCode() <= value.identityCode(); p = p.forward)
			if (p.value != null && (p.value == value || p.value.equals(value))) return p;
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
	@Deprecated
	static final Entry searchEntryDeprecated(final IoDLL value, final Entry entryStart, final Entry entryEnd) {
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
	 * 在指定锁表中间段，找到某个标记的下限(包含本标记)<br/>
	 * 允许倒序查找<br/>
	 * @param entryStart Entry
	 * @param entryEnd Entry
	 * @param value IoDLL
	 * @return Entry
	 */
	static final Entry EntryGetDown(final Entry entryStart, final Entry entryEnd, final IoDLL value) {
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

	/**
	 * 在指定锁表中间段，找到某个标记的下限(包含本标记)<br/>
	 * 允许倒序查找<br/>
	 * @param entryStart Entry
	 * @param entryEnd Entry
	 * @param e Entry
	 * @return Entry
	 */
	static final Entry EntryGetDown(final Entry entryStart, final Entry entryEnd, final Entry e) {
		if (e == null) new StringIndexOutOfBoundsException("Entry is not null!!!");
		if (e.value == null) new StringIndexOutOfBoundsException("IoDLL is not null!!!");
		if (entryStart == null) return null;
		if (entryStart == entryEnd) return entryStart.value.identityCode() > e.value.identityCode() ? entryStart : null;
		for (Entry p = entryStart; p != null; p = p.next) {
			/* 记录  第一个大于这个值的位置 */
			if (p.value.identityCode() >= e.value.identityCode()) return p;
			if (p == entryEnd) break;
		}
		return null;
	}

	/**
	 * 判断链表段的顺序<br/>
	 * 1:从大到小<br/>
	 * 0:从小到大<br/>
	 * -1:乱序<br/>
	 * @param start Entry
	 * @param end Entry
	 * @return int
	 */
	static final int EntryOrderingIs(final Entry start, final Entry end) {
		boolean boolPold = false;
		boolean isFirst = true;
		long pIdentitycode, compIdentitycode;
		for (Entry p = start, comp = (p != null) ? p.next : null; p != null && comp != null; comp = ((p = p.next) == null) ? null : p.next) {
			if (p == end) break;
			if ((pIdentitycode = p.value.identityCode()) == (compIdentitycode = comp.value.identityCode())) continue;
			if (!isFirst || (isFirst = (!isFirst))) if (boolPold ^ pIdentitycode < compIdentitycode) return -1;
			else continue;
			boolPold = pIdentitycode < compIdentitycode;
		}
		if (boolPold) return 0;
		return 1;
	}

	static final boolean EntryAdd(final Entry start, final Entry end, final Entry e) {
		if (start == null) return false;
		//Entry f = null;
		Entry EntryDown = EntryGetDown(start, end, e);
		/* 未找到则 添加链表*/
		if (EntryDown != null) EntryAddForward(EntryDown, e);
		else if (end != null) EntryAddNext(end, e);
		else EntryAddNext(start, e);
		EntryDown = null;
		return true;
	}

	/**
	 * 把单元插入到EntryDown之前的位置上<br/>
	 * @param EntryDown Entry
	 * @param e Entry
	 * @return Entry
	 */
	static final Entry EntryAddForward(final Entry EntryDown, final Entry e) {
		if (e == null || EntryDown == null) return null;
		/* 如果有下限值，则放入下限对象的前面(默认为从小到大的排序)*/
		e.next = EntryDown;
		e.forward = EntryDown.forward;
		if (EntryDown.forward != null) EntryDown.forward.next = e;
		EntryDown.forward = e;
		return e;
	}

	/**
	 * 把单元插入到EntryDown之后的位置上<br/>
	 * @param EntryDown Entry
	 * @param e Entry
	 * @return Entry
	 */
	static final Entry EntryAddNext(final Entry EntryDown, final Entry e) {
		if (e == null || EntryDown == null) return null;
		/* 直接放在EntryDown尾 */
		e.forward = EntryDown;
		e.next = EntryDown.next;
		if (EntryDown.next != null) EntryDown.next.forward = e;
		EntryDown.next = e;
		return e;
	}

	/**
	 * 查找链表最后一位单元<br/>
	 * @param start Entry
	 * @return Entry
	 */
	static final Entry EntryEnd(final Entry start) {
		for (Entry p = start; p != null; p = p.next)
			if (p.next == null) return p;
		return null;
	}

	/**
	 * 把单元移出链表<br/>
	 * 注意：只移出一个单元，如果出现多个相同identityCode的，则只移出第一个<br/>
	 * @param start Entry
	 * @param end Entry
	 * @param delEntry Entry
	 * @return Entry
	 */
	static final Entry EntryRemove(final Entry start, final Entry end, final Entry delEntry) {
		if (start == null) return null;
		for (Entry p = start; p != null; p = p.next) {
			if (p == delEntry || p.value.identityCode() == delEntry.value.identityCode()) {
				if (p.forward != null) p.forward.next = p.next;
				if (p.next != null) p.next.forward = p.forward;
				return p;
			}
			if (p == end) break;
		}
		return null;
	}

	@Override
	public String toString() {
		privateInitCache();
		System.out.println("====================================");
		System.out.println("entryCount:" + entryCount);
		int ii = 1;
		for (Entry p = entryStart; p != null; p = p.next)
			showEntryView(ii++, p);
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
		showEntryView(entryStart);
		System.out.println();
		System.out.print("entryEnd:");
		showEntryView(entryEnd);
		System.out.println();
		System.out.println("----------");
	}

	public void showEntryView(final Entry p) {
		if (p == null) return;
		System.out.print("[" + p.value.identityCode());
		System.out.print("]{" + p.value.identityName() + "} ");
	}

	public void showEntryView(final int i, final Entry p) {
		if (p == null) return;
		System.out.print("(" + i + ")[" + p.value.identityCode());
		System.out.print("]{" + p.value.identityName() + "} ");
	}

	/**
	 * 对象单元
	 * @author Sunjian
	 */
	private static final class Entry {
		/**
		 * 链表前端单元
		 */
		Entry forward;
		IoDLL value;
		/**
		 * 链表后端单元
		 */
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
