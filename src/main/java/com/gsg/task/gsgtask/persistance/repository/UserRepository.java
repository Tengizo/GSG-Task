package com.gsg.task.gsgtask.persistance.repository;

import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import com.gsg.task.gsgtask.persistance.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User repository is kind of communicator between csv file and application.
 * at start it loads all the users and keeps them in Map for further uses.
 */

@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UserRepository {
    private static final String CSV_PATH = "csvDB/users/users.csv";
    private String CSVAbsolutePath;
    private Map<String, User> userMap;
    private Long maxId;

    @PostConstruct
    private void init() {
        createFile();
        this.userMap = getUsers();
        if (userMap.size() == 0)
            createHeaders();
        this.maxId = getMaxId();

    }

    /**
     *
     * @return User object if User exists otherwise null
     */
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userMap.get(username));
    }

    public Optional<User> getUserById(Long id) {
        return userMap.values().stream().filter(e -> e.getId().equals(id)).findAny();
    }

    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * adds user in the user map and adds new line for the new user in csv file
     * it's synchronized because we have parallel tasks running
     * and we don't want these tasks to mess up our csv :)
     */
    public synchronized User addUser(User user) {
        user.setId(generateId());
        writeUser(user, StandardOpenOption.APPEND);
        userMap.put(user.getUsername(), user);
        return user;
    }

    /**
     * updates user in the user Map and updates csv file by rewriting the whole file.
     *  it's synchronized because we have parallel tasks running
     *  and we don't want these tasks to mess up our csv :)
     */
    public synchronized void updateUser(User user) {
        Optional<User> userOp = getUserById(user.getId());
        if (userOp.isEmpty()) {
            throw new AppException(ExceptionType.USER_NOT_FOUND);
        }
        User fromDB = userOp.get();
        userMap.remove(fromDB.getUsername());
        userMap.put(user.getUsername(), user);
        List<User> sorted = userMap.values().stream().sorted(Comparator.comparing(User::getId)).collect(Collectors.toList());
        writeMultipleUser(sorted, StandardOpenOption.CREATE);
    }

    public synchronized void deleteUser(User user) {
        Optional<User> userOp = getUserById(user.getId());
        if (userOp.isEmpty()) {
            throw new AppException(ExceptionType.USER_NOT_FOUND);
        }
        User fromDB = userOp.get();
        userMap.remove(fromDB.getUsername());
        List<User> sorted = userMap.values().stream().sorted(Comparator.comparing(User::getId)).collect(Collectors.toList());
        writeMultipleUser(sorted, StandardOpenOption.CREATE);
    }

    /**
     * writes CSV headers
     */
    private void createHeaders() {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Path.of(CSVAbsolutePath), StandardOpenOption.CREATE);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        CSVFormat.DEFAULT
                                .withHeader(UserHeaders.class)
                                .withFirstRecordAsHeader()
                )
        ) {
            csvPrinter.printRecord(
                    Arrays.stream(UserHeaders.values()).collect(Collectors.toList())
            );
            csvPrinter.flush();
        } catch (IOException e) {
            log.error("Error on CSV write: ", e);
            throw new AppException(ExceptionType.CSV_DB_ERROR);
        }
    }
    /**
     * Creates csv files and directories if needed
     */
    private void createFile() {
        String[] dirs = CSV_PATH.split("/");
        StringBuilder dirPath = new StringBuilder();
        for (int i = 0; i < dirs.length - 1; i++) {
            if (i == 0)
                dirPath.append(dirs[i]);
            else
                dirPath.append(File.separator).append(dirs[i]);

            File file = new File(dirPath.toString());
            if (!file.exists())
                file.mkdir();
        }
        File file = new File(CSV_PATH.replaceAll("//", File.separator));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.CSVAbsolutePath = file.getAbsolutePath();
    }

    private void writeUser(User user, StandardOpenOption... openOptions) {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Path.of(CSVAbsolutePath), openOptions);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        CSVFormat.DEFAULT
                                .withHeader(UserHeaders.class)
                                .withFirstRecordAsHeader()
                )
        ) {
            csvPrinter.printRecord(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getCountry(),
                    user.getJobInterval(),
                    user.getYtVideoLink(),
                    user.getCommentLink()
            );
            csvPrinter.flush();
        } catch (IOException e) {
            log.error("Error on CSV write: ", e);
            throw new AppException(ExceptionType.CSV_DB_ERROR);
        }
    }

    private void writeMultipleUser(Collection<User> users, StandardOpenOption... openOptions) {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Path.of(CSVAbsolutePath), openOptions);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        CSVFormat.DEFAULT
                                .withHeader(UserHeaders.class)
                                .withFirstRecordAsHeader()
                );
        ) {
            csvPrinter.printRecord(
                    Arrays.stream(UserHeaders.values()).collect(Collectors.toList()));
            for (User user : users) {
                csvPrinter.printRecord(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getCountry(),
                        user.getJobInterval(),
                        user.getYtVideoLink(),
                        user.getCommentLink()
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            log.error("Error on CSV write: ", e);
            throw new AppException(ExceptionType.CSV_DB_ERROR);
        }
    }

    private Long generateId() {
        maxId++;
        return maxId;
    }

    private Long getMaxId() {
        if (userMap != null && userMap.size() > 0) {
            User withMaxId = Collections.max(userMap.values(), Comparator.comparing(User::getId));
            return withMaxId.getId();
        }
        return 0L;
    }

    private Map<String, User> getUsers() {
        Map<String, User> users = new HashMap<>();
        try (
                Reader in = new FileReader(new File(CSV_PATH))
        ) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(UserHeaders.class).withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                User user = User.builder()
                        .id(Long.valueOf(record.get(UserHeaders.ID)))
                        .username(record.get(UserHeaders.USERNAME))
                        .password(record.get(UserHeaders.PASSWORD))
                        .country(record.get(UserHeaders.COUNTRY))
                        .jobInterval(Integer.valueOf(record.get(UserHeaders.JOB_INTERVAL)))
                        .ytVideoLink(record.get(UserHeaders.YT_VIDEO_LINK))
                        .commentLink(record.get(UserHeaders.COMMENT_LINK))
                        .build();
                users.put(user.getUsername(), user);

            }
        } catch (IOException e) {
            log.error("Error on CSV read: ", e);
            throw new AppException(ExceptionType.CSV_DB_ERROR);
        }
        return users;
    }

}

