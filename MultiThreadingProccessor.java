 public List<RunExecution> scenarioExecutor(RunExecution runExecution) throws Exception {

        Cloner cloner = new Cloner();
        List<RunExecution> runExecutions = new ArrayList<>();
        for(int i = 1; i <= runExecution.getExecutionCount(); i++){
            RunExecution cloned = (RunExecution) cloner.deepClone(runExecution);
            cloned.setItterationId(i);
            runExecutions.add(cloned);
        }
        Stopwatch stopwatchTotal = Stopwatch.createStarted();
        //RandomUtils.nextInt(1000, 5000);
        // create a pool of threads, 10 max jobs will execute in parallel
        ExecutorService threadPool = Executors.newFixedThreadPool(runExecution.getTestScenarios().size());
        // submit jobs to be executing by the pool
        int itterationCounter = 1;
        for (RunExecution runExecution1 : runExecutions) {
            // clean resources
            logger.info("Iteration cont =: {}", itterationCounter );
            Stopwatch stopwatchIteration = Stopwatch.createStarted();
            List<Future> futures = new ArrayList<Future>();
            for (TestScenario testScenario : runExecution1.getTestScenarios()) {
                futures.add(threadPool.submit(new Callable<Void>() {
                    public Void call() throws IOException, InterruptedException {
                        callYourMethod(testScenario);
                        return null;
                    }
                }));
            }
            for (Future f : futures) {
                try {
                    f.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            logger.info("Total Time for iteration: " + stopwatchIteration.stop());
            itterationCounter ++;
        }
        logger.info("Total Time: " + stopwatchTotal.stop());
        // once you've submitted your last job to the service it should be shut down
        threadPool.shutdown();
        // wait for the threads to finish if necessary
        threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        return runExecutions;
    }
