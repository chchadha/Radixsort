package apps;

import java.io.IOException;
import java.util.Scanner;

import structures.Node;

/**
 * This class sorts a given list of strings which represent numbers in
 * the given radix system. For instance, radix=10 means decimal numbers;
 * radix=16 means hexadecimal numbers. 
 *
 * @author ru-nb-cs112
 */
public class Radixsort {

	/**
	 * Master list that holds all items, starting with input, and updated after every pass
	 * of the radixsort algorithm. Holds sorted result after the final pass. This is a
	 * circular linked list in which every item is stored in its textual string form (even
	 * though the items represent numbers). This masterListRear field points to the last 
	 * node in the CLL.
	 */
	Node<String> masterListRear;

	/**
	 * Array of linked lists that holds the digit-wise distribution of the items during
	 * each pass of the radixsort algorithm. 
	 */
	Node<String>[] buckets;

	/**
	 * The sort radix, defaults to 10.
	 */
	int radix=10;

	/**
	 * Initializes this object with the given radix (10 or 16)
	 *
	 * @param radix
	 */
	public Radixsort() {
		masterListRear = null;
		buckets = null;
	}

	/**
	 * Sorts the items in the input file, and returns a CLL containing the sorted result
	 * in ascending order. The first line in the input file is the radix. Every subsequent
	 * line is a number, to be read in as a string.
	 *
	 * The items in the input are first read and stored in the master list, which is a CLL that is referenced
	 * by the masterListRear field. Next, the max number of digits in the items is determined. Then, 
	 * scatter and gather are called, for each pass through the items. Pass 0 is for the least
	 * significant digit, pass 1 for the second-to-least significant digit, etc. After each pass,
	 * the master list is updated with items in the order determined at the end of that pass.
	 *
	 * NO NEW NODES are created in the sort process - the nodes of the master list are recycled
	 * through all the intermediate stages of the sorting process.
	 *
	 * @param sc Scanner that points to the input file of radix + items to be sorted
	 * @return Sorted (in ascending order) circular list of items
	 * @throws IOException If there is an exception in reading the input file
	 */
	public Node<String> sort(Scanner sc)
			throws IOException {
		// first line is radix
		if (!sc.hasNext()) { // empty file, nothing to sort
			return null;
		}

		// read radix from file, and set up buckets for linked lists
		radix = sc.nextInt();
		buckets = (Node<String>[])new Node[radix];

		// create master list from input
		createMasterListFromInput(sc);

		// find the string with the maximum length
		int maxDigits = getMaxDigits();

		for (int i=0; i < maxDigits; i++) {
			scatter(i);
			gather();
		}

		return masterListRear;
	}

	/**
	 * Reads entries to be sorted from input file and stores them as 
	 * strings in the master CLL (pointed by the instance field masterListRear, 
	 * in the order in which they are read. In other words, the first entry in the linked 
	 * list is the first entry in the input, the second entry in the linked list is the 
	 * second entry in the input, and so on. 
	 *
	 * @param sc Scanner pointing to the input file
	 * @throws IOException If there is any error in reading the input
	 */
	public void createMasterListFromInput(Scanner sc)
			throws IOException {
		// WRITE YOUR CODE HERE

		// This method is the same as addingToRear in a CLL. The CLL is following a First In First Out pattern.

		if(sc.hasNext() == false){
			throw new IOException("The text file is empty");
			// If the file has no data then the program will throw an exception

		}else{

			while(sc.hasNext() == true){ //This while loop will continue iterating as long as there is data in the scanner file to add.

				Node<String> newNode = new Node<String>(sc.next(), null);

				/* This line creates a new node where it takes the input from scanner
				 * but has not initialized a location in the CLL for the node. Everytime the while loop iterates
				 *  a new node is created with the next line of data
				 */

				if(masterListRear == null){ //if the circular linked list is empty

					masterListRear = newNode;
					newNode.next = masterListRear;
					/* If the CLL is empty then the program will set the already given pointer, MasterListRear
					 * to the newNode and then point back to itself.
					 */
				}

				newNode.next = masterListRear.next;
				masterListRear.next = newNode;
				masterListRear = masterListRear.next;

				/* If the pointer is not null and is pointing to a node then the program will
				 * add a new node in the CLL by first pointing the newNode's next to whatever
				 * masterListRear.next is pointing to (always the first node) and then insert the
				 * newNode after masterListRear and lastly shift the pointer one over to make the
				 * masterListRear point to the last element in the CLL.
				 */
			}
		}

	}

	/**
	 * Determines the maximum number of digits over all the entries in the master list
	 *
	 * @return Maximum number of digits over all the entries
	 */
	public int getMaxDigits() {
		int maxDigits = masterListRear.data.length();
		Node<String> ptr = masterListRear.next;
		while (ptr != masterListRear) {
			int length = ptr.data.length();
			if (length > maxDigits) {
				maxDigits = length;
			}
			ptr = ptr.next;
		}
		return maxDigits;
	}

	/**
	 * Scatters entries of master list (referenced by instance field masterListReat)
	 * to buckets for a given pass.
	 *
	 * Passes are digit by digit, starting with the rightmost digit -
	 * the rightmost digit is the "0-th", i.e. pass=0 for rightmost digit, pass=1 for
	 * second to rightmost, and so on.
	 *
	 * Each digit is extracted as a character,
	 * then converted into the appropriate numeric value in the given radix
	 * using the java.lang.Character.digit(char ch, int radix) method
	 *
	 * @param pass Pass is 0 for rightmost digit, 1 for second to rightmost, etc
	 */
	public void scatter(int pass) {
		// WRITE YOUR CODE HERE

		Node<String> ptr = masterListRear.next;
		buckets = (Node<String>[])new Node[radix];


		/* Before beginning the linked list I define a pointer which points to the front of the masterListRear. There
		 * are two pointers needed for the method. Both pointers point to the front of the masterList, but ptr iterates
		 * while the front pointer will stay in place. The buckets are created of size radix. Therefore if there are just numbers then the radix is 10 and
		 * we will need a buckets array of size 10 or (9 indices). Otherwise if there are letters in the txt file then
		 * the radix will be size 16.
		 *
		*/
		Node<String> front = masterListRear.next; // Second pointer which keeps track of the front node

		/* In the do while we first check if the string data in each node of the pointer is less than the pass
		 * +1. If the pass is 0 then we must check the index at the data.length()-(1+pass). If the pass is greater
		 * than the length of the data at the node ptr is pointing to then the program will place the node in the 0 bucket
		 * to serve as a placeholder until all the passes have been made for all the items in the masterList. Otherwise
		 * the loop will iterate through the buckets array and check if the digit at pass is equal to the index of the
		 * bucket. We create a CLL within the buckets by pointing the buckets to the respective nodes. The outer loop iterates
		 * until the pointer in the front of the masterList points to itself and  the inner for loop keep tracks of the increment
		 * in buckets.
		 *
		 */
		do{
			String data = ptr.data;
			if(data.length() < (pass+1)){

				if(buckets[0]==null){

					buckets[0] = ptr;
					ptr = ptr.next;
					buckets[0].next = buckets[0];
				}
				else{

					Node<String> bucketFront = buckets[0].next;
					buckets[0].next = ptr;
					ptr = ptr.next;
					buckets[0] = buckets[0].next;
					buckets[0].next = bucketFront;
				}
			}
			else{
				for(int i =0; i<buckets.length; i++){

					if(java.lang.Character.digit(ptr.data.charAt(ptr.data.length()-(pass+1)), radix) == i){

						if(buckets[i]==null){

							buckets[i] = ptr;
							ptr = ptr.next;
							buckets[i].next = buckets[i];
							break;
						}

						else{

							Node<String> bucketFront = buckets[i].next;
							buckets[i].next = ptr;
							ptr = ptr.next;
							buckets[i] = buckets[i].next;
							buckets[i].next = bucketFront;
							break;
						}
					}
				}
			}

		}while(ptr!= front);

	}

	/**
	 * Gathers all the CLLs in all the buckets into the master list, referenced
	 * by the instance field masterListRear
	 *
	 * @param buckets Buckets of CLLs
	 */
	public void gather() {
		// WRITE YOUR CODE HERE

		masterListRear = null;

		/*Currently masterListRear is pointing to the entries from the txt file.
		 *In order to gather all the items and reference them with masterListRear, we
		 *must erase the old linked list to create a brand new one.
		 */

		int i = 0; //counter which will be used to check if the program has gone through all the buckets

		while(i < buckets.length){
			// The while loop is iterating through the buckets from 0 to 9

			Node<String> bucketsRear = buckets[i];

			if(bucketsRear!=null){

				if(masterListRear == null){
					masterListRear = bucketsRear;

					/* As we go through the buckets of the program we check if masterListRear pointed
					 * to any node. If it masterListRear is null then then masterListRear is set to bucket[i] which is the rear
					 * of the bucket which is already in a CLL. Otherwise the masterListRear is set to the front of the bucket
					 * and the masterListRear pointer is moved to the last node in buckets. Then the last node in buckets points
					 * to the front of the masterListRear list.
					 */
				}
				else{

					Node<String> masterFront = masterListRear.next;
					masterListRear.next=buckets[i].next;
					masterListRear=buckets[i];
					buckets[i].next = masterFront;

				}

			}

			i++;
		}
	}
}





