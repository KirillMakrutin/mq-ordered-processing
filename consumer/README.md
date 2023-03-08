Some approaches are described here: [Thread pool to process messages in parallel, but preserve order within conversations](https://stackoverflow.com/questions/44327619/thread-pool-to-process-messages-in-parallel-but-preserve-order-within-conversat)
and in the [example](https://rextester.com/IWPLAV52962)

Currently implemented solution works in single running consumer environment only. If you have multiple running instances of a consumer, each instance will receive a subset of the messages published to the queue. This means that the ordering of messages may not be preserved across all instances of the consumer.

To ensure that message ordering is preserved across multiple instances of a consumer, you can use a partitioning strategy that assigns each groupId to a specific partition. Each partition is then assigned to a single instance of the consumer, which ensures that messages with the same groupId are processed in order by the same consumer instance.

One way to implement this partitioning strategy is to use a consistent hashing algorithm to map groupId values to a fixed number of partitions. Each consumer instance is then assigned one or more partitions to process, and messages with the same groupId are always assigned to the same partition.

For example, suppose we have three consumer instances and we want to partition messages into six partitions. We can use a consistent hashing algorithm to map groupId values to a partition number between 1 and 6. Messages with the same groupId will always be assigned to the same partition, ensuring that they are processed in order by the same consumer instance.

Note that implementing a partitioning strategy can add some complexity to your system, as you need to ensure that each partition is processed by a single consumer instance. You may also need to implement some form of load balancing to ensure that each consumer instance is processing a roughly equal number of partitions.

Further ideas:
<ol>
  <li>Consider synchronization and ordering on DB</li>
  <li>Use distributed computing service</li>
  <li>Partitioning</li>
</ol>