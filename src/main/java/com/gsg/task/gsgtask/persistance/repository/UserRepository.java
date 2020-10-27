package com.gsg.task.gsgtask.persistance.repository;

import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import com.gsg.task.gsgtask.persistance.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UserRepository {
    private static final String CSV_PATH = "users/users.csv";
    @Value("classpath:" + CSV_PATH)
    private Resource resourceFile;
    private Map<String, User> userMap;
    private Long maxId;

    @PostConstruct
    private void init() {
        this.userMap = getUsers();
        this.maxId = getMaxId();

    }

    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userMap.get(username));
    }

    public Optional<User> getUserById(Long id) {
        return userMap.values().stream().filter(e -> e.getId().equals(id)).findAny();
    }

    public void addUser(User user) {
        user.setId(generateId());
        writeUser(user, StandardOpenOption.APPEND);
    }

    public void updateUser(User user) {
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

    public void deleteUser(User user) {
        Optional<User> userOp = getUserById(user.getId());
        if (userOp.isEmpty()) {
            throw new AppException(ExceptionType.USER_NOT_FOUND);
        }
        User fromDB = userOp.get();
        userMap.remove(fromDB.getUsername());
        List<User> sorted = userMap.values().stream().sorted(Comparator.comparing(User::getId)).collect(Collectors.toList());
        writeMultipleUser(sorted, StandardOpenOption.CREATE);
    }

    private void writeUser(User user, StandardOpenOption... openOptions) {
        try (
                BufferedWriter writer = Files.newBufferedWriter(resourceFile.getFile().toPath(), openOptions);
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
            log.error("Error on CSV write: ",e);
            throw new AppException(ExceptionType.CSV_DB_ERROR);
        }
    }

    private void writeMultipleUser(Collection<User> users, StandardOpenOption... openOptions) {
        try (
                BufferedWriter writer = Files.newBufferedWriter(resourceFile.getFile().toPath(), openOptions);
                CSVPrinter csvPrinter = new CSVPrinter(writer,
                        CSVFormat.DEFAULT
                                .withHeader(UserHeaders.class)
                                .withFirstRecordAsHeader()
                );
        ) {
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
            log.error("Error on CSV write: ",e);
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
                Reader in = new FileReader(new ClassPathResource(CSV_PATH).getFile())
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
            log.error("Error on CSV read: ",e);
            throw new AppException(ExceptionType.CSV_DB_ERROR);
        }
        return users;
    }

}

