package MaQiao.MaQiaoOrderedDoubleLinkedList;

import java.util.ArrayList;
import java.util.List;
import MaQiao.Constants.Constants;
import MaQiao.MaQiaoOrderedDoubleLinkedList.Consts;
import static MaQiao.MaQiaoOrderedDoubleLinkedList.Consts.booleanType;
import static MaQiao.MaQiaoOrderedDoubleLinkedList.Consts.sequence;

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
	 * 转序(有锁)<br/>
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

	//TODO put 添加单元
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
	 * 添加单元，手动决定是否覆盖(有锁)<br/>
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
	 * 添加单元，直接在链表后面加入单元(有锁)<br/>
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

	//TODO get 获取单元
	/**
	 * 按接口得到单元<br/>
	 * @param value IoDLL
	 * @return Entry
	 */
	public final Entry get(final IoDLL value) {
		if (value == null) return null;
		final boolean order = isOrdering() == 1;
		final long identityCode = value.identityCode();
		for (Entry p = (order) ? this.entryEnd : this.entryStart; p != null && p.value.identityCode() <= identityCode; p = (order) ? p.forward : p.next)
			if (p.value.identityCode() == identityCode && p.value == value) return p;
		return null;
	}

	/**
	 * 按链表顺序得到单元<br/>
	 * @param order int
	 * @return Entry
	 */
	public final Entry get(final int num) {
		return get(num, false);
	}

	/**
	 * 按链表顺序或倒序得到单元<br/>
	 * ordering:<br/>
	 * 顺序 false<br/>
	 * 倒序 true<br/>
	 * @param Num int
	 * @param ordering boolean
	 * @return Entry
	 */
	public final Entry get(final int Num, final boolean ordering) {
		if (Num <= 0 || Num > entryCount) return null;
		Entry p = (ordering) ? entryEnd : entryStart;
		for (int i = 1; p != null; p = (ordering) ? p.forward : p.next)
			if (i++ == Num) return p;
		return p;
	}

	/**
	 * 按标识值得到单元<br/>
	 * @param identityCode
	 * @return Entry
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

	//TODO remove 移除单元

	/**
	 * 按接口得到单元<br/>
	 * @param value IoDLL
	 * @return Entry
	 */
	public final boolean remove(final IoDLL value) {
		if (value == null) return false;
		final boolean order = isOrdering() == 1;
		final long identityCode = value.identityCode();
		for (Entry p = (order) ? this.entryEnd : this.entryStart; p != null && p.value.identityCode() <= identityCode; p = (order) ? p.forward : p.next)
			if (p.value.identityCode() == identityCode && p.value == value) {
				remove(p);
				return true;
			}
		return false;
	}

	/**
	 * 按链表顺序值移除单元<br/>
	 * @param order int
	 * @return boolean
	 */
	public final boolean remove(final int num) {
		return remove(num, false);
	}

	/**
	 * 按链表顺序或倒序移除单元<br/>
	 * ordering:<br/>
	 * 顺序 false<br/>
	 * 倒序 true<br/>
	 * @param Num int
	 * @param ordering boolean
	 * @return boolean
	 */
	public final boolean remove(final int Num, final boolean ordering) {
		if (Num <= 0 || Num > entryCount) return false;
		Entry p = (ordering) ? entryEnd : entryStart;
		for (int i = 1; p != null; p = (ordering) ? p.forward : p.next)
			if (i++ == Num) {
				remove(p);
				return true;
			}
		return false;
	}

	/**
	 * 按标识值移除单元(有锁)<br/>
	 * @param identityCode
	 */
	public final boolean remove(final long identityCode) {
		locked();
		try {
			Entry p = entryStart;
			if (p == null) return false;
			for (; p != null; p = p.next) {
				if (p.value.identityCode() == identityCode) remove(p);
				if (p == this.entryEnd) break;
			}
			p = null;
			return true;
		} finally {
			unLocked();
		}

	}

	/**
	 * 移除某个单元<br/>
	 * @param p Entry
	 */
	private final void remove(final Entry p) {
		if (p == null) return;
		if (p.forward != null) p.forward.next = p.next;
		if (p.next != null) p.next.forward = p.forward;
	}

	/**
	 * 移除链表头(有锁)<br/>
	 */
	public final boolean removeFirst() {
		locked();
		try {
			if (this.entryStart == null) return false;
			(this.entryStart = this.entryStart.next).forward = null;
			return true;
		} finally {
			unLocked();
		}
	}

	/**
	 * 移除链表尾(有锁)<br/>
	 * @return boolean
	 */
	public final boolean removeLast() {
		locked();
		try {
			if (this.entryEnd == null) return false;
			(this.entryEnd = this.entryEnd.forward).next = null;
			return true;
		} finally {
			unLocked();
		}
	}

	//TODO contains 判断是否存在
	/**
	 * 判断接口是否存在，以标识数为标准(有锁)<br/>
	 * @param value IoDLL
	 * @param eStart Entry
	 * @param eEnd Entry
	 * @return boolean
	 */
	public final boolean contains(final IoDLL value) {
		locked();
		try {
			return (EntrySearch(value, entryStart, entryEnd) != null);
		} finally {
			unLocked();
		}
	}

	/**
	 * 把乱序的链表顺序重置，按从小到大重新整理(有锁)<br/>
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

	/**
	 * 链表转成数组<br/>
	 * @return IoDLL[]
	 */
	public final IoDLL[] toArray() {
		final IoDLL[] newArray = new IoDLL[this.entryCount];
		if (this.entryCount > 0) {
			int i = 0;
			for (Entry p = entryStart; p != null; p = p.next)
				newArray[i++] = p.value;
		}
		return newArray;
	}

	/**
	 * 链表转成List<br/>
	 * @return List< IoDLL >
	 */
	public final List<IoDLL> toList() {
		final List<IoDLL> newList = new ArrayList<IoDLL>(this.entryCount);
		if (entryCount > 0) for (Entry p = entryStart; p != null; p = p.next)
			newList.add(p.value);
		return newList;
	}

	/**
	 * 清空链表(有锁)<br/>
	 */
	public final void clear() {
		locked();
		try {
			for (Entry p = entryStart; p != null; p = p.next) {
				p.value = null;
				p.forward = null;
			}
			this.entryStart = this.entryEnd = null;
			this.entryCount = 0;
			for (int i = 0, len = this.cacheEntry.length; i < len; i++)
				this.cacheEntry[i] = null;
		} finally {
			unLocked();
		}

	}

	/*
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				压栈，弹栈

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	 */
	/**
	 * 压栈(有锁)<br/>
	 * 忽略顺序<br/>
	 * @param value IoDLL
	 * @return boolean
	 */
	public final boolean stackPush(final IoDLL value) {
		if (value == null) return false;
		locked();
		try {
			/* 未找到则 添加链表*/
			privateAddEntry(this.entryStart, value);
			return true;
		} finally {
			unLocked();
		}
	}

	/**
	 * 弹栈(有锁)<br/>
	 * 忽略顺序<br/>
	 * @param value IoDLL
	 * @return IoDLL
	 */
	public final IoDLL stackPop() {
		if (this.entryStart == null) return null;
		locked();
		try {
			Entry e = this.entryStart;
			IoDLL f = e.value;
			this.entryStart = this.entryStart.next;
			if (this.entryStart != null && this.entryStart.forward != null) this.entryStart.forward = null;
			if (this.entryEnd == e) this.entryEnd = null;
			this.entryCount--;
			return f;
		} finally {
			unLocked();
		}
	}

	/**
	 * 栈长<br/>
	 * @return int
	 */
	public final int stackLength() {
		return this.entryCount;
	}

	/**
	 * 清空栈表<br/>
	 */
	public final void stackClear() {
		clear();
	}

	/*
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

				队列

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	 */
	/**
	 * 队列添加(有锁)<br/>
	 * @param value IoDLL
	 * @return boolean
	 */
	public final boolean queueAdd(final IoDLL value) {
		if (value == null) return false;
		locked();
		try {
			privateAddEntry(null, value);
			return true;
		} finally {
			unLocked();
		}
	}

	/**
	 * 移除队列中排序第num的单元<br/>
	 * @param num int
	 * @return boolean
	 */
	public final boolean queueRemove(final int num) {
		if (this.entryStart == null) return false;
		return remove(num, false);
	}

	/**
	 * 移除并返问队列头部的元素<br/>
	 * @return IoDLL
	 */
	public final IoDLL queuePoll() {
		final Entry e = this.entryStart;
		this.entryStart = this.entryStart.next;
		if (this.entryEnd == e) this.entryEnd = null;
		return e.value;
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
		if (this.entryCount > Consts.cacheCount) {
			final int maxSize = this.entryCount / (Consts.cacheLen - 1);
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
		if (this.entryStart == null) {
			this.entryStart = this.entryEnd = e;
			this.entryCount++;
			return;
		}
		/* 如果下限为空，则认为全部链表的所有值都小于等于此标记值，直接放在队尾 */
		if (EntryDown == null) {
			e.forward = entryEnd;
			e.next = null;
			this.entryEnd.next = e;
			this.entryEnd = e;
		} else {
			/* 如果有下限值，则放入下限对象的前面(默认为从小到大的排序)*/
			if (this.entryStart == EntryDown) this.entryStart = e;
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
	 * Consts.sequence.Reverse:从大到小<br/>
	 * Consts.sequence.Ordering:从小到大<br/>
	 * Consts.sequence.Outoforder:乱序<br/>
	 * @param start Entry
	 * @param end Entry
	 * @return Consts.sequence
	 */
	static final sequence EntrySequence(final Entry start, final Entry end) {
		boolean boolPold = false, isFirst = true;
		long code1, code2;
		for (Entry p = start, comp = (p != null) ? p.next : null; p != null && comp != null; comp = ((p = p.next) == null) ? null : p.next) {
			if (p == end) break;
			if ((code1 = p.value.identityCode()) == (code2 = comp.value.identityCode())) continue;
			if (!isFirst || (isFirst = (!isFirst))) if (boolPold ^ code1 < code2) return sequence.Outoforder;
			else continue;
			boolPold = code1 < code2;
		}
		if (boolPold) return sequence.Ordering;
		return sequence.Reverse;
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
		return EntrySequence(start, end).value;
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
	public static final class Entry {
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
