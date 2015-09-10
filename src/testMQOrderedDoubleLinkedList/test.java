package testMQOrderedDoubleLinkedList;

import org.junit.Test;

//import common.Rnd;
import MaQiao.MaQiaoOrderedDoubleLinkedList.IoDLL;
import MaQiao.MaQiaoOrderedDoubleLinkedList.MQOrderedDoubleLinkedList;

public class test {

	@SuppressWarnings({ "deprecation" })
	@Test
	public void testuser() {
		//MQOrderedDoubleLinkedList odll=new MQOrderedDoubleLinkedList();
//		for(int i=10;i<16;i++){
//			int ii=Rnd.getRndInt(10, 28);
//			//user u=new user("user"+i,(long)(100+i));
//			user u=new user("user"+ii,(long)(100+ii));
//			odll.put(u);
//		}
//		odll.toString();
		MQOrderedDoubleLinkedList odll2=new MQOrderedDoubleLinkedList();
		odll2.put(new user("user103",103));
		odll2.put(new user("user104",104));
		odll2.put(new user("user101",101));
		odll2.put(new user("user107",107));
		odll2.put(new user("user105",105));
		odll2.put(new user("user102",102));
		odll2.put(new user("user117",104));
		odll2.put(new user("user10333",103));
		//odll2.Ordering();
		odll2.toString();
		odll2.putFast(new user("user121",121));
		odll2.putFast(new user("user120",120));
		odll2.putFast(new user("user119",119));
		odll2.toString();
		odll2.orderingReset();
		odll2.toString();
		//odll2.toString();
		//odll.Ordering();
		//odll.toString();
	}

	public static class user implements IoDLL {
		private long identityCode = 0;
		private String identityName = "";

		public user(final String identityName, final long identityCode) {
			this.identityName = identityName;
			this.identityCode = identityCode;
		}

		@Override
		public String identityName() {
			return identityName;
		}

		@Override
		public long identityCode() {
			return identityCode;
		}

	}

}
