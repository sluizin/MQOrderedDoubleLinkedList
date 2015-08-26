package MaQiao.MaQiaoOrderedDoubleLinkedList;
/**
 * 有序两端双向链表，对象接口(所有此接口的对象都可以进入链表)
 * @author Sunjian
 * @since 1.7
 *
 */
public interface IoDLL {
	/**
	 * 对象的名称，用于链表时toString()时输出使用
	 */
	public String identityName();
	/**
	 * 对象的排序值，用于链表的有向的排列位置
	 */
	public long identityCode();
}
